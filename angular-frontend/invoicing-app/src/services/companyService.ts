import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environement } from '../environements/environement';
import { Company } from '../app/company';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root', // <-- This makes the service available application-wide
})
export class CompanyService {
  private contentType = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  };

  private PATH = 'companies';

  constructor(private http: HttpClient) {}

  getCompanies(): Observable<Company[]> {
    return this.http.get<Company[]>(this.apiUrl(this.PATH));
  }

  addCompanies(company: Company): Observable<any> {
    return this.http.post<any>(
      this.apiUrl(this.PATH),
      this.toCompanyRequest(company),
      this.contentType
    );
  }

  deleteCompanies(id: number): Observable<any> {
    return this.http.delete<any>(this.apiUrl(this.PATH + '/' + 'delete', id));
  }

  editCompany(company: Company) {
    return this.http.put<any>(
      this.apiUrl(this.PATH + '/' + 'update', company.id),
      this.toCompanyRequest(company),
      this.contentType
    );
  }

  private apiUrl(service: string, id: number = null): string {
    const idInUrl = id !== null ? '/' + id : '';

    return environement.apiUrl + '/' + service + idInUrl;
  }

  private toCompanyRequest(company: Company) {
    return {
      id: company.id,
      name: company.name,
      taxIdentification: company.taxIdentification,
      address: company.address,
      pensionInsurance: company.pensionInsurance,
      healthInsuranceBaseValue: company.healthInsuranceBaseValue,
    };
  }
}
