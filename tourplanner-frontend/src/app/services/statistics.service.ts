import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TourStats {
  tourId: number;
  tourName: string;
  totalLogs: number;
  averageRating: number;
  averageDifficulty: number;
  averageDistance: number;
  averageTime: number;
  popularity: number;
  childFriendliness: number;
}

@Injectable({ providedIn: 'root' })
export class StatisticsService {
  private baseUrl = '/api/statistics';

  constructor(private http: HttpClient) {}

  getTourStatistics(tourId: number): Observable<TourStats> {
    return this.http.get<TourStats>(`${this.baseUrl}/tour/${tourId}`);
  }
}
