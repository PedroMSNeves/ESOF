describe('Administration walkthrough', () => {
  beforeEach(() => {
    cy.demoAdminLogin();

    cy.get('[data-cy="administrationMenuButton"]').click();
    cy.get('[data-cy="manageCoursesMenuButton"]').click({force: true});
  });

  afterEach(() => {
    cy.logout();
  });

  it('login creates a course execution adds a teacher though the form and deletes the course execution', () => {
    cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');
    cy.addUserThroughForm('TEST-AO3', 'User1', 'test@mail.com', "TEACHER");
    cy.deleteCourseExecution('TEST-AO3');
  });

  
  it('login creates two course executions and deletes it', () => {
    cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');
    cy.log('try to create with the same name');
    cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');
    cy.closeErrorMessage();
    cy.log('close dialog');
    cy.get('[data-cy="cancelButton"]').click();
    cy.deleteCourseExecution('TEST-AO3');
  });


  it('login creates FROM a course execution and deletes it', () => {
    cy.createFromCourseExecution('Demo Course', 'TEST-AO3', 'Winter Semester');

    cy.deleteCourseExecution('TEST-AO3');
  });

  it('login as admin, creates a new course execution, add one student, delete student and the course execution', () => {
    cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');
    cy.addUserThroughForm('TEST-AO3', 'User1', 'test@mail.com', "STUDENT");
    cy.deleteUser('test@mail.com', 'TEST-AO3');
    cy.deleteCourseExecution('TEST-AO3');
  });

  it('login as admin, creates a new course execution, try to add the same student twice, delete student and the course execution', () => {
    cy.createCourseExecution('Demo Course', 'TEST-AO3', 'Spring Semester');
    cy.addUserThroughForm('TEST-AO3', 'User1', 'test@mail.com', "STUDENT");
    cy.addUserThroughForm('TEST-AO3', 'User1', 'test@mail.com', "STUDENT");
    cy.contains('Error: Duplicate user: test@mail.com');
    cy.closeErrorMessage();
    cy.deleteUser('test@mail.com', 'TEST-AO3');
    cy.deleteCourseExecution('TEST-AO3');
  });

});
