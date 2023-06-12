const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
/**
 * Export a function. Accept the base config as the only param.
 *
 * @param {Parameters<typeof rootWebpackConfig>[0]} options
 */

module.exports = async ({ config, mode }) => {

  const tsPaths = new TsconfigPathsPlugin({
    configFile: './tsconfig.base.json',
  });
  config.devtool = 'source-map';
  config.resolve.plugins
    ? config.resolve.plugins.push(tsPaths)
    : (config.resolve.plugins = [tsPaths]);

  return config;
};
