describe('Teacher Dashboard Stats', () => {

    beforeEach( () => {
        /*cy.request('http://localhost:8081/auth/demo/teacher')
        .as('loginResponse')
        .then((response) => {
        Cypress.env('token', response.body.token);
        return response;
      });*/
      cy.demoTeacherLogin();
    });

    //um teste
    it('teacher access dashboard of 2023', () => {
        cy.intercept('GET', '**/teachers/dashboards/executions/*').as('getTeacherDashboard');
       
        //go to the courses menu
        cy.get('[data-cy="coursesMenuButton"]').click();
        cy.contains('2st semester 2023/2024').parent().find('[data-cy="courseButton"]').click();
        
        //go to the dashboard menu
        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getTeacherDashboard');

        //compare the values
        cy.get('[data-cy="numMore75CorrectQuestions"]').should('have.text', 10);
        cy.get('[data-cy="numStudents"]').should('have.text', 21);
        cy.get('[data-cy="numMore75CorrectQuestions"]').should('have.text', 10);
        cy.get('[data-cy="numAtLeast3Quizzes"]').should('have.text', 21);
        cy.get('[data-cy="numQuizzes"]').should('have.text', 21);
        cy.get('[data-cy="numUniqueAnsweredQuizzes"]').should('have.text', 10);
        cy.get('[data-cy="averageQuizzesSolved"]').should('have.text', 21);
        cy.get('[data-cy="numQuestions"]').should('have.text', 21);        
        cy.get('[data-cy="numUniqueQuestionsSolved"]').should('have.text', 10);
        cy.get('[data-cy="averageQuestionsSolved"]').should('have.text', 21);
        
        
        //compare the charts
        cy.get('[data-cy="barChartStudentStats"]') .scrollIntoView().wait(5000).screenshot('base-screenshots/expeted/studentStats2023');
        cy.get('[data-cy="barChartQuizStats"]')    .scrollIntoView().wait(5000).screenshot('base-screenshots/expeted/quizStats2023');
        cy.get('[data-cy="barChartQuestionStats"]').scrollIntoView().wait(5000).screenshot('base-screenshots/expeted/questionStats2023');
        
        cy.get('[data-cy="barChartStudentStats"]') .scrollIntoView().wait(5000).screenshot('test-pics/studentStats2023');
        cy.get('[data-cy="barChartQuizStats"]')    .scrollIntoView().wait(5000).screenshot('test-pics/quizStats2023');
        cy.get('[data-cy="barChartQuestionStats"]').scrollIntoView().wait(5000).screenshot('test-pics/questionStats2023');
        cy.compareScreenshots('./tests/e2e/screenshots/dashboard/teacherDashboardStats.js/base-screenshots/expeted/studentStats2023.png',
         './tests/e2e/screenshots/dashboard/teacherDashboardStats.js/test-pics/studentStats2023.png');
        cy.compareScreenshots('./tests/e2e/screenshots/dashboard/teacherDashboardStats.js/base-screenshots/expeted/quizStats2023.png',
         './tests/e2e/screenshots/dashboard/teacherDashboardStats.js/test-pics/quizStats2023.png');
        cy.compareScreenshots('./tests/e2e/screenshots/dashboard/teacherDashboardStats.js/base-screenshots/expeted/questionStats2023.png',
         './tests/e2e/screenshots/dashboard/teacherDashboardStats.js/test-pics/questionStats2023.png');

      });
});