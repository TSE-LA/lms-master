describe("Dashboard Page", () => {
  beforeEach(() => {
  cy.visit("https://test-lms.erin.systems/");
    cy.get('input[type="email"]').type('admin');
      cy.get('input[type="password"]').type('secret');
      cy.get('button').click();
  });

  it("Check online course plus button", () => {
    cy.get('[class=dashlet]').should('exist');
    cy.get('[class=title]').eq(2).should("have.text","ЦАХИМ СУРГАЛТ");
    cy.get('li').eq(2).click()
    cy.get('.margin-top > jrs-button > .primary')
    cy.get('button').eq(3).click()
    cy.get('.input-style').type('Шинэ суралцагчийн сургалт');
              cy.get('jrs-dropdown-input').eq(1).click()
              cy.get('li').eq(13).click()
              cy.get('jrs-dropdown-input').eq(2).click()
              cy.get('.content-wrapper').eq(1).click()
              cy.get('jrs-dropdown-input').eq(3).click()
              cy.get('.content-wrapper').eq(1).click()
              cy.get('jrs-dropdown-input').eq(4).click()
              cy.get('.content-wrapper').eq(2).click()
              cy.get('jrs-text-area').type('Шинэ суралцагчийн чиглүүлэх сургалттай танилцана уу.');
              cy.get('button').eq(5).click()
              cy.contains("Тесттэй эсэх?");
              cy.get("jrs-checkbox input").eq(3).check();
              cy.get("jrs-circle-button").eq(2).click();
  });
});
