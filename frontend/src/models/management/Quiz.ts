import Question from "@/models/management/Question";

export class Quiz {
  id!: number;
  number!: number;
  scramble!: boolean;
  title!: string;
  date!: string | Date;
  creationDate!: string | Date;
  availableDate!: string | Date;
  conclusionDate!: string | Date;
  type!: string;
  year!: number;
  series!: number;
  version!: string;
  numberOfQuestions!: number;
  numberOfAnswers!: number;

  questions: Question[] = [];

  constructor(jsonObj?: Quiz) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.number = jsonObj.number;
      this.scramble = jsonObj.scramble;
      this.title = jsonObj.title;
      this.type = jsonObj.type;
      this.year = jsonObj.year;
      this.series = jsonObj.series;
      this.version = jsonObj.version;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.numberOfAnswers = jsonObj.numberOfAnswers;

      if (jsonObj.date) this.date = new Date(jsonObj.date);

      if (jsonObj.creationDate)
        this.creationDate = new Date(jsonObj.creationDate);

      if (jsonObj.availableDate)
        this.availableDate = new Date(jsonObj.availableDate);

      if (jsonObj.conclusionDate)
        this.conclusionDate = new Date(jsonObj.conclusionDate);

      if (jsonObj.questions) {
        this.questions = jsonObj.questions.map(
          (question: Question) => new Question(question)
        );
      }
    }
  }

  get stringDate(): string {
    if (this.date) {
      return this.date.toLocaleString("pt");
    }
    return "-";
  }

  get stringCreationDate(): string {
    if (this.creationDate) {
      return this.creationDate.toLocaleString("pt");
    }
    return "-";
  }

  get stringAvailableDate(): string {
    if (this.availableDate) {
      return this.availableDate.toLocaleString("pt");
    }
    return "-";
  }

  get stringConclusionDate(): string {
    if (this.conclusionDate) {
      return this.conclusionDate.toLocaleString("pt");
    }
    return "-";
  }
}