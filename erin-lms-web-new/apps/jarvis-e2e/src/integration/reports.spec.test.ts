function assertTableRow (
  rowId: string,
  name: string,
  type: string,
  state: string,
  enrollmentCount: string,
  totalViewers: string,
  completedViewers: string,
  certificate: string,
  receivedViewers: string,
  repeatedViewersCount: string,
  testScore: string,
  averageSpentTimeOnTest: string) {
  cy.getTestId(rowId).get("#name").should("contain.text", name);
  cy.getTestId(rowId).get("#type").should("contain.text", type);
  cy.getTestId(rowId).get("#state").should("contain.text", state);
  cy.getTestId(rowId).get("#enrollmentCount").should("contain.text", enrollmentCount);
  cy.getTestId(rowId).get("#totalViewers").should("contain.text", totalViewers);
  cy.getTestId(rowId).get("#completedViewers").should("contain.text", completedViewers);
  cy.getTestId(rowId).get("#receivedViewers").should("contain.text", receivedViewers);
  cy.getTestId(rowId).get("#repeatedViewersCount").should("contain.text", repeatedViewersCount);
  cy.getTestId(rowId).get("#testScore").should("contain.text", testScore);
  cy.getTestId(rowId).get("#averageSpentTimeOnTest").should("contain.text", averageSpentTimeOnTest);
}

describe("Report Tests", () => {
  beforeEach(() => {
    cy.login("admin", "secret");
  });

  it("check report page", () => {
    cy.get(".menu-item").eq(1).click();
    cy.get(".report-header .tab-group li").should("have.length", 5);
    cy.get(".report-header .tab-group li").eq(0).should("have.text", "Цахим сургалт");
    cy.getTestId("column-filter-name").type("123");
    cy.getTestId("table-row-0").should("have.length", 1);
    assertTableRow(
      "table-row-0",
      "123",
      "Англи хэл",
      "СУРАЛЦАГЧ",
      "0",
      "0",
      "0",
      "Үгүй",
      "0",
      "0",
      "0/0",
      "00:00:00"
    );
    cy.get(".menu-item").eq(2).click();
    cy.getTestId("course-name-123").find(".card-button").click();

  });
});
