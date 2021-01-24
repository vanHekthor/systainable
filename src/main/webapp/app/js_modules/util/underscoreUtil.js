/**
 * This function replaces characters in string arrays or object keys. Non-break spaces and underscores are supported.
 * @param charToReplace character that shall be replaced
 * @param attributes string array or object
 * @returns {(Array|object)} String array with replaced characters or object with renamed keys
 */
function switchCharacters(charToReplace, attributes) {
  if (Array.isArray(attributes)) {
    const noUnderscoreStringArray = attributes.map(function (string) {
      if (charToReplace === '_') {
        return string.replace(/_/g, '\xa0');
      } else if (charToReplace === '\xa0') {
        return string.replace('\xa0', '_');
      } else {
        throw Error(`Wrong character: Replacement of this character "${charToReplace}" is not supported!`);
      }
    });
    return noUnderscoreStringArray;
  } else {
    const noUnderscoreKeyObj = {};
    Object.keys(attributes).forEach(function (key) {
      if (charToReplace === '_') {
        noUnderscoreKeyObj[key.replace(/_/g, '\xa0')] = attributes[key];
      } else if (charToReplace === '\xa0') {
        noUnderscoreKeyObj[key.replace('\xa0', '_')] = attributes[key];
      } else {
        throw Error(`Wrong character: Replacement of this character "${charToReplace}" is not supported!`);
      }
    });
    return noUnderscoreKeyObj;
  }
}

export default {
  /**
   * This method replaces underscores in string arrays or object keys with non-break spaces
   * @param attributes String array or object
   * @returns {Array|Object} No-underscore string array or object with no underscores in its keys
   */
  replaceUnderscores: function (attributes) {
    if (typeof attributes === 'object') {
      return switchCharacters('_', attributes);
    } else {
      throw Error('Wrong type: array of property strings or object with property keys needed!');
    }
  },

  /**
   * This method replaces non-break spaces in string arrays or object keys with underscores
   * @param attributes String array or object
   * @returns {Array|Object} No-non-break-space string array or object with no non-break spaces in its keys
   */
  replaceNonBreakSpaces: function (attributes) {
    if (typeof attributes === 'object') {
      return switchCharacters('\xa0', attributes);
    } else {
      throw Error('Wrong type: array of property strings or object with property keys needed!');
    }
  },
};
