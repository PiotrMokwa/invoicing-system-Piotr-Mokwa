import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Company } from './company';
import { FormsModule } from '@angular/forms';
import { CompanyService } from '../services/companyService';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],

  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'invoicing-app';
  companies: Company[] = [];
  newCompany: Company = new Company(0, '', '', '', 0, 0);
  text: any;

  constructor(private companiesService: CompanyService) {}

  ngOnInit(): void {
    this.companiesService.getCompanies().subscribe((company) => {
      this.companies = company;
    });
  }
  addCompany() {
    this.companiesService.addCompanies(this.newCompany).subscribe((id) => {
      this.newCompany.id = id;
      this.companies.push(this.newCompany);
      this.newCompany = new Company(0, '', '', '', 0, 0);
    });
  }

  deleteCompany(companyToDelete: Company) {
    console.log('delete');
    console.log('id companyToDelete.i', companyToDelete.id);
    this.companiesService.deleteCompanies(companyToDelete.id).subscribe(() => {
      this.companies = this.companies.filter(
        (company) => company !== companyToDelete
      );
    });
  }

  triggerUpdate(company: Company) {
    company.editedCompany = new Company(
      company.id,
      company.name,
      company.taxIdentification,
      company.address,
      company.pensionInsurance,
      company.healthInsuranceBaseValue
    );
    company.editMode = true;
  }

  cancelCompanyUpdate(company: Company) {
    company.editMode = false;
  }

  updateCompany(updatedCompany: Company) {
    this.companiesService
      .editCompany(updatedCompany.editedCompany)
      .subscribe(() => {
        updatedCompany.name = updatedCompany.editedCompany.name;
        updatedCompany.taxIdentification =
          updatedCompany.editedCompany.taxIdentification;
        updatedCompany.address = updatedCompany.editedCompany.address;
        updatedCompany.pensionInsurance =
          updatedCompany.editedCompany.pensionInsurance;
        updatedCompany.healthInsuranceBaseValue =
          updatedCompany.editedCompany.healthInsuranceBaseValue;

        updatedCompany.editMode = false;
      });
  }
}
