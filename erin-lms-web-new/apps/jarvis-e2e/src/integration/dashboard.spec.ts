describe("Dashboard Page", () => {
  beforeEach(() => {
  cy.visit("https://test-lms.erin.systems/");
    cy.get('input[type="email"]').type('admin');
      cy.get('input[type="password"]').type('secret');
      cy.get('button').click();
  });

  it("Check dashboard", () => {
    cy.get('[class=dashlet]').should('exist');
    cy.get('[class=title]').eq(2).should("have.text","ЦАХИМ СУРГАЛТ");
  });
});

