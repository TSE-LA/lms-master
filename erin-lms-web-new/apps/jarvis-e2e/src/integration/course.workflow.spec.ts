describe("Workflow test", () => {
  beforeEach(() => cy.login("admin", "secret"));

  it.only("Create course", () => {
    cy.get('[class=title]').eq(2).should("have.text", "цахим СУРГАЛТ");
    cy.get('.menu-item').eq(2).click();
    cy.getTestId('course-create-button').click();
    cy.getTestId('course-name-input-field').type('Шинэ суралцагчийн сургалт');
    cy.getTestId('course-category-dropdown-field').click();
    cy.get('li.mat-menu-item').eq(3).click();
    cy.getTestId("course-type-dropdown-field").click();
    cy.get('li.mat-menu-item').eq(2).click();
    cy.getTestId("course-certificate-dropdown-field").click();
    cy.get('li.mat-menu-item').eq(2).click();
    cy.getTestId("next-button").click();
    cy.get('jrs-header-text').should("have.text", "ЦАХИМ СУРГАЛТЫН МЭДЭЭЛЭЛ");
  });
  it("publish course", () => {
    cy.get('input[type="email"]').type('admin');
    cy.get('input[type="password"]').type('secret');
    cy.get('button').click();
    cy.get('[class=dashlet]').should('exist');
    cy.get('[class=title]').eq(2).should("have.text", "ЦАХИМ СУРГАЛТ");
    cy.get('li').eq(2).click();
    cy.get('.card-button').eq(0).click();
    cy.get('jrs-header-text').eq(0).should('exist');
    cy.get('jrs-header-text').should("have.text", "ЦАХИМ СУРГАЛТЫН МЭДЭЭЛЭЛ");
    cy.get('button').eq(6).click();
    cy.get('jrs-online-course-enrollment').eq(0).should('exist');
    cy.get('.title-container').eq(0).should("have.text", "СУРГАЛТАД ХАМРАГДАХ СУРАЛЦАГЧ");
    cy.get('button').eq(12).click();
    cy.get('.header-container').eq(0).should('exist');
    cy.get('.title').eq(3).should("have.text", "ХАМРАГДАХ СУРАЛЦАГЧ");
    cy.get('jrs-checkbox').eq(3).click();
    cy.get('button').eq(4).click();
    cy.get('button').eq(3).click();
    cy.get('button').eq(6).click();
    cy.get('jrs-online-course-enrollment').eq(0).should('exist');
    cy.get('.title-container').eq(0).should("have.text", "СУРГАЛТАД ХАМРАГДАХ СУРАЛЦАГЧ");
    cy.get('.mat-menu-trigger > jrs-icon > .jrs-font > .icon-text').click();
    cy.get('.mat-menu-content > :nth-child(2)').click();
    cy.get('.jrs-dialog-buttons > :nth-child(1) > .primary').click();
  });
});


