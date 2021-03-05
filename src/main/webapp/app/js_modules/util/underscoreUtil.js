/**
 * This function replaces characters in a single string, string arrays or object keys. Non-break spaces and underscores are supported.
 * @param charToReplace character that shall be replaced
 * @param strings string array or object
 * @returns {(Array|object)} String array with replaced characters or object with renamed keys
 */
function switchCharacters(charToReplace, strings) {
  if (typeof strings === 'string') {
    if (charToReplace === '_') {
      return strings.replace(/_/g, '\xa0');
    } else if (charToReplace === '\xa0') {
      return strings.replace(/\u00a0/g, '_');
    }
  } else if (Array.isArray(strings)) {
    const noUnderscoreStringArray = strings.map(function (string) {
      if (charToReplace === '_') {
        return string.replace(/_/g, '\xa0');
      } else if (charToReplace === '\xa0') {
        return string.replace(/\u00a0/g, '_');
      } else {
        throw Error(`Wrong character: Replacement of this character "${charToReplace}" is not supported!`);
      }
    });
    return noUnderscoreStringArray;
  } else {
    const noUnderscoreKeyObj = {};
    Object.keys(strings).forEach(function (key) {
      if (charToReplace === '_') {
        noUnderscoreKeyObj[key.replace(/_/g, '\xa0')] = strings[key];
      } else if (charToReplace === '\xa0') {
        noUnderscoreKeyObj[key.replace(/\u00a0/g, '_')] = strings[key];
      } else {
        throw Error(`Wrong character: Replacement of this character "${charToReplace}" is not supported!`);
      }
    });
    return noUnderscoreKeyObj;
  }
}

export default {
  /**
   * This method replaces underscores in a single string, string arrays or object keys with non-break spaces
   * @param strings String array or object
   * @returns {Array|Object} No-underscore string array or object with no underscores in its keys
   */
  replaceUnderscores: function (strings) {
    if (typeof strings === 'string' || typeof strings === 'object') {
      return switchCharacters('_', strings);
    } else {
      throw Error('Wrong type: array of property strings or object with property keys needed!');
    }
  },

  /**
   * This method replaces non-break spaces in string arrays or object keys with underscores
   * @param strings String array or object
   * @returns {Array|Object} No-non-break-space string array or object with no non-break spaces in its keys
   */
  replaceNonBreakSpaces: function (strings) {
    if (typeof strings === 'string' || typeof strings === 'object') {
      return switchCharacters('\xa0', strings);
    } else {
      throw Error('Wrong type: array of property strings or object with property keys needed!');
    }
  },
};
