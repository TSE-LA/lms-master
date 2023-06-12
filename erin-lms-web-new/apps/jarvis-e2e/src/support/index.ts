// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************
// Import commands.js using ES2015 syntax:
/// <reference types="cypress" />

// eslint-disable-next-line @typescript-eslint/no-namespace
declare namespace Cypress {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  interface Chainable<Subject> {
    login(username: string, password: string): void;
    getTestId(selector: string): Chainable<HTMLElement>;
  }
}

//
// -- This is a parent command --
Cypress.Commands.add("login", (username, password) => {
  cy.visit("http://localhost:4201/");
  cy.get('input[type="email"]').type(username);
  cy.get('input[type="password"]').type(password);
  cy.get('button').click();
  cy.get('[class=dashlet]').should('exist');
});

Cypress.Commands.add("getTestId", (selector) => {
  return cy.get(`[data-test-id=${selector}]`);
});
