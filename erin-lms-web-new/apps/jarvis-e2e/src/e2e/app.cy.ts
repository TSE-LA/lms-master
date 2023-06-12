describe("jarvis", () => {
  beforeEach(() => cy.visit("https://test-lms.erin.systems/"));

  it("should display welcome message", () => {
    // Custom command example, see `../support/commands.ts` file
    cy.get('input[type="email"]').type('admin');
    cy.get('input[type="password"]').type('secret');
    cy.get('button').click();


    // Function helper example, see `../support/app.po.ts` file
  });
});
