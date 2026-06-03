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

export interface OverallStats {
  totalTours: number;
  totalLogs: number;
  totalDistance: number;
  totalTimeMinutes: number;
  averageTourDistance: number;
  averageTourTimeMinutes: number;
  averageRating: number;
  averageDifficulty: number;
}

export interface TourStatsSummary {
  tourId: number;
  tourName: string;
  logCount: number;
  totalDistance: number;
  totalTimeMinutes: number;
  averageRating: number;
  averageDifficulty: number;
  childFriendliness: number;
}

export interface TransportTypeStats {
  toursByTransportType: { [key: string]: number };
  avgDistanceByTransportType: { [key: string]: number };
}

export interface UserStatistics {
  overallStats: OverallStats;
  tourStats: TourStatsSummary[];
  transportTypeStats: TransportTypeStats;
}

@Injectable({ providedIn: 'root' })
export class StatisticsService {
  private baseUrl = '/api/statistics';

  constructor(private http: HttpClient) {}

  getTourStatistics(tourId: number): Observable<TourStats> {
    return this.http.get<TourStats>(`${this.baseUrl}/tour/${tourId}`);
  }

  getUserStatistics(): Observable<UserStatistics> {
    return this.http.get<UserStatistics>(this.baseUrl);
  }
}