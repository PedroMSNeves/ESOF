package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;

public class QuizStatsDto {
    
    public Integer id;
    private int numQuizzes = 0;
    private int uniqueQuizzesSolved = 0;
    private float averageQuizzesSolved = 0;

    public QuizStatsDto(){

    }

    public QuizStatsDto(QuizStats quizStats){
        setId(quizStats.getId());
        setNumQuizzes(quizStats.getNumQuizzes());
        setUniqueQuizzesSolved(quizStats.getUniqueQuizzesSolved());
        setAverageQuizzesSolved(quizStats.getAverageQuizzesSolved());
    }


    public Integer getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public int getNumQuizzes(){
        return this.numQuizzes;
    }

    public void setNumQuizzes(int numQuizzes){
        this.numQuizzes=numQuizzes;
    }

    public int getUniqueQuizzesSolved(){
        return this.uniqueQuizzesSolved;
    }

    public void setUniqueQuizzesSolved(int uniqueQuizzesSolved){
        this.uniqueQuizzesSolved=uniqueQuizzesSolved;
    }

    public float getAverageQuizzesSolved(){
        return this.averageQuizzesSolved;
    }

    public void setAverageQuizzesSolved(float averageQuizzesSolved){
        this.averageQuizzesSolved=averageQuizzesSolved;
    } 
}