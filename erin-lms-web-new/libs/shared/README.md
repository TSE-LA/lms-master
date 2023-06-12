# ERIN Learning Management System storybook

## Common commands

### Create module in a lib

> npx ng g lib ‘module-name’

### Generate component in project

> npx ng g component ‘component-name’ --project=shared

### Generate component without style or html

> npx ng g component comp-name --project=shared --inlineTemplate=true --inlineStyle=true

### Generate module in project

> npx ng g m module-name --project=core

### Generate component inside module

> npx ng g component module-name/comp-name --project=core --inlineTemplate=true --inlineStyle=true
