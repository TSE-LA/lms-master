const rootMain = module.exports = {
  stories: [],
  addons: ['@storybook/addon-knobs']
};

// Use the following syntax to add addons!
// rootMain.addons.push('');
rootMain.stories.push(
  ...['../src/lib/**/*.stories.mdx', '../src/lib/**/*.stories.@(js|jsx|ts|tsx)']
);

rootMain.addons.push('@storybook/addon-actions');
rootMain.addons.push('@storybook/addon-docs');
rootMain.addons.push('@storybook/addon-viewport')



module.exports = rootMain;
