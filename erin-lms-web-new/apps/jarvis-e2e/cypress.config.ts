import { defineConfig } from "cypress";
import { nxE2EPreset } from "@nrwl/cypress/plugins/cypress-preset";

const cypressJsonConfig = {
  "fileServerFolder": ".",
  "fixturesFolder": "./src/fixtures",
  "integrationFolder": "./src/integration",
  "modifyObstructiveCode": false,
  "supportFile": "./src/support/index.ts",
  "pluginsFile": false,
  "video": true,
  "videosFolder": "../../dist/cypress/apps/jarvis-e2e/videos",
  "screenshotsFolder": "../../dist/cypress/apps/jarvis-e2e/screenshots",
  "videoUploadOnPasses" : true,
  "chromeWebSecurity": false,
  "projectId": "jp5z88"
}
export default defineConfig({
  e2e: {
    ...nxE2EPreset(__dirname),
    ...cypressJsonConfig
  },
});
