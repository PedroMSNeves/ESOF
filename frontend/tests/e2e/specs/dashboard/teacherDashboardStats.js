describe('Teacher Dashboard Stats', () => {

    beforeEach( () => {
        //colocar stats diferentes para todos os anos
        cy.request('http://localhost:8081/auth/demo/teacher')
        .as('loginResponse')
        .then((response) => {
        Cypress.env('token', response.body.token);
        return response;
      });
    date = new Date();
    cy.demoTeacherLogin();
    //colocar aqui os dados das course execution caso nao sejam criadas no populate demo

    });

    //um teste
    it('teacher access dashboard', () => {
        cy.intercept('GET', '**/teacher/dashboard/').as('getTeacherDashboard');
       
        //go to the courses menu
        cy.get('[data-cy]="coursesMenuButton').click();
        cy.get('[data-cy]="courseSquare').should('contain', ).click();

        //go to the dashboard menu
        cy.get('[data-cy]="dashboardMenuButton').click();


        //compara os valores
        cy.get('[data-cy]="numStudents"').should(have.value, );
        cy.get('[data-cy]="numMore75CorrectQuestions"').should(have.value, );
        cy.get('[data-cy]="numAtLeast3Quizzes"').should(have.value, );

        cy.get('[data-cy]="numQuizzes"').should(have.value, );
        cy.get('[data-cy]="numUniqueAnsweredQuizzes"').should(have.value, );
        cy.get('[data-cy]="averageQuizzesSolved"').should(have.value, );

        cy.get('[data-cy]="numQuestions"').should(have.value, );        
        cy.get('[data-cy]="numUniqueQuestionsSolved"').should(have.value, );
        cy.get('[data-cy]="averageQuestionsSolved"').should(have.value, );
        
        //compara os graficos
        cy.get('[data-cy]="barChartStudentStats"').matchImageSnapshot('barChartStudentStats');
        cy.get('[data-cy]="barChartQuizStats"').matchImageSnapshot('barChartQuizStats');
        cy.get('[data-cy]="barChartQuestionStats"').matchImageSnapshot('barChartQuestionStats');
    });
});