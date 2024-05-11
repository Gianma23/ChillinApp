class KeyTranslator:

    def translate_keys_in_dictionary(self,dictionary):
        translated_dictionary = {}
        for key, value in dictionary.items():
            translated_key = ""
            i = 0
            while i < len(key):
                if key[i] == '@':
                    if i+2 < len(key) and key[i+1] == '@':
                        translated_key += '@'
                        i += 2
                    else:
                        translated_key += '.' + key[i+1]
                        i += 2
                else:
                    translated_key += key[i]
                    i += 1
            translated_dictionary[translated_key] = value
        print(translated_dictionary)
        return translated_dictionary
