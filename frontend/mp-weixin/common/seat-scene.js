function normalizeSeatScene(scene) {
  var normalized = typeof scene === 'string' ? scene.trim() : '';

  for (var i = 0; i < 2 && normalized; i++) {
    try {
      var decoded = decodeURIComponent(normalized);
      if (decoded === normalized) break;
      normalized = decoded;
    } catch (e) {
      break;
    }
  }

  return normalized.trim();
}

module.exports = {
  normalizeSeatScene: normalizeSeatScene
};
