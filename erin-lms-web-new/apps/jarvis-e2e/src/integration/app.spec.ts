describe("Login Page", () => {
  beforeEach(() => cy.visit("https://test-lms.erin.systems/"));

  it("should login on correct credentials", () => {

    // LMS-LOGIN-01
    cy.get('input[type="email"]').type('khulan');
    cy.get('input[type="password"]').type('Secret123');
    cy.get('button').click();
  });

  it("shouldn't login because unregistered user", () => {
    //  LMS-LOGIN-02
    cy.get('input[type="email"]').type('sambuu');
    cy.get('input[type="password"]').type('Secret');
    cy.get('button').click();
  });

    it("shouldn't login on wrong password", () => {

    // LMS-LOGIN-03
        cy.get('input[type="email"]').type('khulan');
        cy.get('input[type="password"]').type('Secret12');
        cy.get('button').click();

      });

    it("shouldn't login because unregistered in the group", () => {

    // LMS-LOGIN-04
        cy.get('input[type="email"]').type('bilguun');
        cy.get('input[type="password"]').type('Secret123');
        cy.get('button').click();

      });

    it("shouldn't login on wrong username", () => {

    // LMS-LOGIN-05
        cy.get('input[type="email"]').type('hulan');
        cy.get('input[type="password"]').type('Secret123');
        cy.get('button').click();
       // cy.get('.error').should('Unauthorized')
      });

    it("shouldn't login because archived user", () => {

    // LMS-LOGIN-06
         cy.get('input[type="email"]').type('amgaa');
         cy.get('input[type="password"]').type('Secret123');
         cy.get('button').click();

      });
});
