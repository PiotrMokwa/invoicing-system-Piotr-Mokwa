export class Company {
  public editMode: boolean = false;
  public editedCompany: Company | null = null;

  constructor(
    public id: number,
    public name: string,
    public taxIdentification: string,
    public address: string,
    public pensionInsurance: number,
    public healthInsuranceBaseValue: number
  ) {}
}
