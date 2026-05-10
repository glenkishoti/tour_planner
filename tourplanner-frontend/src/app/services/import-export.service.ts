import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ImportExportService {
  private baseUrl = '/api/import-export';

  constructor(private http: HttpClient) {}

  exportAllTours(): Observable<string> {
    return this.http.get(`${this.baseUrl}/export`, {
      responseType: 'text'
    });
  }

  importTours(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.baseUrl}/import`, formData);
  }
}
