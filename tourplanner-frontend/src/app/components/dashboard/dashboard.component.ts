import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TourService } from '../../services/tour.service';
import { TourLogService } from '../../services/tour-log.service';
import { AuthService } from '../../services/auth.service';
import { Tour, TourRequest } from '../../models/tour';
import { TourLog, TourLogRequest } from '../../models/tour-log';
import { TourFormComponent } from '../tour-form/tour-form.component';
import { TourLogFormComponent } from '../tour-log-form/tour-log-form.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, TourFormComponent, TourLogFormComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  tours: Tour[] = [];
  selectedTour: Tour | null = null;
  tourLogs: TourLog[] = [];

  showTourForm = false;
  editingTour: Tour | null = null;

  showLogForm = false;
  editingLog: TourLog | null = null;

  searchQuery = '';

  constructor(
    private tourService: TourService,
    private tourLogService: TourLogService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadTours();
  }

  loadTours(): void {
    this.tourService.getAll().subscribe(tours => this.tours = tours);
  }

  selectTour(tour: Tour): void {
    this.selectedTour = tour;
    this.tourLogService.getAll(tour.id).subscribe(logs => this.tourLogs = logs);
  }

  openCreateTour(): void {
    this.editingTour = null;
    this.showTourForm = true;
  }

  openEditTour(tour: Tour): void {
    this.editingTour = tour;
    this.showTourForm = true;
  }

  onTourSaved(): void {
    this.showTourForm = false;
    this.loadTours();
  }

  deleteTour(id: number): void {
    if (!confirm('Delete this tour?')) return;
    this.tourService.delete(id).subscribe(() => {
      this.loadTours();
      if (this.selectedTour?.id === id) this.selectedTour = null;
    });
  }

  openCreateLog(): void {
    this.editingLog = null;
    this.showLogForm = true;
  }

  openEditLog(log: TourLog): void {
    this.editingLog = log;
    this.showLogForm = true;
  }

  onLogSaved(): void {
    this.showLogForm = false;
    if (this.selectedTour) this.selectTour(this.selectedTour);
  }

  deleteLog(logId: number): void {
    if (!confirm('Delete this log?')) return;
    this.tourLogService.delete(this.selectedTour!.id, logId).subscribe(() => {
      if (this.selectedTour) this.selectTour(this.selectedTour);
    });
  }

  get filteredTours(): Tour[] {
    if (!this.searchQuery) return this.tours;
    const q = this.searchQuery.toLowerCase();
    return this.tours.filter(t =>
      t.name.toLowerCase().includes(q) ||
      t.from.toLowerCase().includes(q) ||
      t.to.toLowerCase().includes(q) ||
      t.transportType.toLowerCase().includes(q)
    );
  }

  formatTime(minutes: number): string {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }
}
