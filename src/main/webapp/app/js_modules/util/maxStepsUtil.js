/**
 * Counts how many linear steps can be made until minimum or maximum value is reached given a start value and a step size.
 * @param startValue Start value
 * @param min Lower limit
 * @param max Upper limit
 * @param stepSize Step size
 * @returns {number} Max. step count
 */
function maxLinearSteps(startValue, min, max, stepSize) {
  let v = startValue;
  let maxSteps = 0;
  let steps = 0;

  while (v > min) {
    v -= stepSize;
    steps++;
  }
  if (steps > maxSteps) {
    maxSteps = steps;
  }

  v = startValue;
  steps = 0;

  while (v < max) {
    v += stepSize;
    steps++;
  }
  if (steps > maxSteps) {
    maxSteps = steps;
  }
  return maxSteps;
}

/**
 * Counts how many exponential steps can be made until minimum or maximum value is reached given a start value and base.
 * @param startValue start value
 * @param min Lower limit
 * @param max Upper limit
 * @param base Base
 * @returns {number} Max. step count
 */
function maxExponentialSteps(startValue, min, max, base) {
  let v = startValue;
  let maxSteps = 0;
  let steps = 0;

  while (v > min) {
    v /= base;
    steps++;
  }
  if (steps > maxSteps) {
    maxSteps = steps;
  }

  v = startValue;
  steps = 0;

  while (v < max) {
    v += v * (base - 1);
    steps++;
  }
  if (steps > maxSteps) {
    maxSteps = steps;
  }
  return maxSteps;
}

export { maxLinearSteps, maxExponentialSteps };
