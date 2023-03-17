package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

// import
// pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import java.io.Serializable;

public class QuestionStatsDto implements Serializable {

    private Integer id;
    private int numQuestionsAvailable = 0;
    private int numQuestionsAnsweredUniq = 0;
    private float averageQuestionsAnsweredUniq = 0;

    public QuestionStatsDto() {
    }

    public QuestionStatsDto(QuestionStats questionstat) {
        id = questionstat.getId();
        numQuestionsAvailable = questionstat.getNumQuestionsAvailable();
        numQuestionsAnsweredUniq = questionstat.getNumQuestionsAnsweredUniq();
        averageQuestionsAnsweredUniq = questionstat.getAverageQuestionsAnsweredUniq();
    }

    // GET
    public Integer getId() {
        return this.id;
    }

    public int getNumQuestionsAvailable() {
        return this.numQuestionsAvailable;
    }

    public int getNumQuestionsAnsweredUniq() {
        return this.numQuestionsAnsweredUniq;
    }

    public float getAverageQuestionsAnsweredUniq() {
        return this.averageQuestionsAnsweredUniq;
    }

    // SET

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumQuestionsAvailable(int numQuestionsavailable) {
        this.numQuestionsAvailable = numQuestionsavailable;
    }

    public void setNumQuestionsAnsweredUniq(int num) {
        this.numQuestionsAnsweredUniq = num;
    }

    public void setAverageQuestionsAnsweredUniq(float averageQuestionsAnsweredUniq) {
        this.averageQuestionsAnsweredUniq = averageQuestionsAnsweredUniq;
    }

    @Override
    public String toString() {
        return "QuestionStatsDto{" + "id = " + getId() +
                ",NumQuestionsAvailable = " + getNumQuestionsAvailable() +
                ",NumQuestionsAnsweredUniq = " + getNumQuestionsAnsweredUniq() +
                ",averageQuestionsAnsweredUniq = " + getAverageQuestionsAnsweredUniq() +
                "}";
    }
}