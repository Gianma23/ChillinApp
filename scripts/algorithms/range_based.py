# Import necessary libraries
import numpy as np  # Numerical computing library
from scipy.stats import invgamma  # Inverse gamma distribution from scipy.stats

# Define BayesianAnalyzer class
class BayesianAnalyzer:
    # Constructor with optional alpha and beta prior parameters
    def __init__(self, alpha_prior=0.01, beta_prior=0.01):
        self.alpha_prior = alpha_prior  # Alpha prior parameter
        self.beta_prior = beta_prior    # Beta prior parameter

    # Method to calculate posterior distribution
    def calculate_posterior(self, data, k=0.1, mu_prior=0, tau2_prior=1, nu_prior=100):
        # Initialize lists to store posterior results
        mu_posterior = []   # Posterior mean
        sigma2_posterior = []  # Posterior variance
        lower_bound = []    # Lower bound of confidence interval
        upper_bound = []    # Upper bound of confidence interval
        posterior_results = []  # List to store posterior results

        # Check if data length is sufficient for training
        if len(data) >= 120:
            n_train = 120  # Number of training samples
            y_train = [d['heartrateSensor'] for d in data[:n_train]]  # Extract heart rate data for training

            # Initialize initial values for mean and variance
            mu_i = np.mean(y_train)  # Initial mean
            sigma2_i = np.var(y_train)  # Initial variance

            # Iterate over remaining data points
            for j in range(n_train, len(data)):
                # Update mean using normal distribution
                mu_i = np.random.normal(loc=np.mean([d['heartrateSensor'] for d in data[:j]]), scale=np.sqrt(sigma2_i/n_train))
                # Update variance using inverse gamma distribution
                sigma2_i = invgamma.rvs(a=(n_train+self.alpha_prior)/2, scale=(np.sum([(d['heartrateSensor']-mu_i)**2 for d in data[:j]])+self.beta_prior)/2)

                # Calculate lower and upper bounds of confidence interval
                lower_bound.append(mu_i - k * np.sqrt(sigma2_i))
                upper_bound.append(mu_i + k * np.sqrt(sigma2_i))

                # Append posterior results
                posterior_results.append({
                    'lower_bound': mu_i - k * np.sqrt(sigma2_i),
                    'upper_bound': mu_i + k * np.sqrt(sigma2_i),
                    'timestamp': data[j]['timestamp']
                })

        return posterior_results  # Return posterior results

    # Method to find the best k parameter
    def find_best_k(self, df, n_train):
        # Initialize list to store overall accuracies
        overall_accuracies = []

        # Iterate over unique participant IDs in the dataframe
        for participant_id in df["Participant"].unique():
            participant_data = df[df["Participant"] == participant_id]  # Filter data for the current participant
            y_i = participant_data["HR"]  # Extract heart rate data

            # Split data into training and testing sets
            y_train = y_i[:n_train]  # Training data
            mu_i = np.mean(y_train)  # Initial mean
            sigma2_i = np.var(y_train)  # Initial variance

            mu_posterior = []  # List to store posterior means
            sigma2_posterior = []  # List to store posterior variances

            # Iterate over remaining data points
            for j in range(n_train, len(y_i)):
                # Update mean using normal distribution
                mu_i = np.random.normal(loc=np.mean(y_i[:j]), scale=np.sqrt(sigma2_i/n_train))
                # Update variance using inverse gamma distribution
                sigma2_i = invgamma.rvs(a=(n_train+self.alpha_prior)/2, scale=(np.sum((y_i[:j]-mu_i)**2)+self.beta_prior)/2)

                # Append posterior mean and variance
                mu_posterior.append(mu_i)
                sigma2_posterior.append(sigma2_i)

            accuracies = []  # List to store accuracies for different k values

            # Iterate over k values
            for k in np.linspace(0.01, 0.9, 9):
                labels = []  # List to store predicted labels
                for mu, sigma2 in zip(mu_posterior, sigma2_posterior):
                    # Calculate lower and upper bounds of confidence interval
                    lower_bound = mu - k * np.sqrt(sigma2)
                    upper_bound = mu + k * np.sqrt(sigma2)
                    # Predict label based on confidence interval
                    if participant_data["Label"].iloc[0] == 1:
                        if mu < lower_bound or mu > upper_bound:
                            labels.append(1)  # Outlier detected
                        else:
                            labels.append(0)  # Inlier detected
                    else:
                        if mu < lower_bound or mu > upper_bound:
                            labels.append(0)  # Inlier detected
                        else:
                            labels.append(1)  # Outlier detected

                # Calculate accuracy
                accuracy = np.mean(labels == participant_data["Label"].iloc[90:])
                accuracies.append((k, accuracy))  # Store k and accuracy

            # Find the best k and corresponding accuracy
            best_k, best_accuracy = max(accuracies, key=lambda x: x[1])
            overall_accuracies.append((participant_id, best_k, best_accuracy))  # Store participant ID, best k, and accuracy

        return overall_accuracies  # Return overall accuracies
