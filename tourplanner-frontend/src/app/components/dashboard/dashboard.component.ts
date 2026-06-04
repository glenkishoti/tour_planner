import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TourService } from '../../services/tour.service';
import { TourLogService } from '../../services/tour-log.service';
import { AuthService } from '../../services/auth.service';
import { SearchService } from '../../services/search.service';
import { ImportExportService } from '../../services/import-export.service';
import { StatisticsService, TourStats, UserStatistics } from '../../services/statistics.service';
import { Tour, TourRequest } from '../../models/tour';
import { TourLog, TourLogRequest } from '../../models/tour-log';
import { TourFormComponent } from '../tour-form/tour-form.component';
import { TourLogFormComponent } from '../tour-log-form/tour-log-form.component';
import { TourMapComponent } from '../tour-map/tour-map.component';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, TourFormComponent, TourLogFormComponent, TourMapComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  tours: Tour[] = [];
  selectedTour: Tour | null = null;
  tourLogs: TourLog[] = [];
  tourStats: TourStats | null = null;

  showTourForm = false;
  editingTour: Tour | null = null;
  showLogForm = false;
  editingLog: TourLog | null = null;

  showStats = false;
  userStats: UserStatistics | null = null;
  statsLoading = false;

  sidebarOpen = false;

  searchQuery = '';
  private searchSubject = new Subject<string>();
  isSearching = false;

  importError = '';
  importSuccess = '';

  constructor(
    private tourService: TourService,
    private tourLogService: TourLogService,
    public authService: AuthService,
    private searchService: SearchService,
    private importExportService: ImportExportService,
    private statisticsService: StatisticsService
  ) {}

  ngOnInit(): void
  {
    this.loadTours();

    // Debounce search — waits 400ms after user stops typing before calling backend
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(query => {
      if (query.trim()) {
        this.isSearching = true;
        this.searchService.searchTours(query).subscribe({
          next: tours => {
            this.tours = tours;
            this.isSearching = false;
          },
          error: () => {
            this.isSearching = false;
          }
        });
      } else {
        this.loadTours();
      }
    });
  }

  loadTours(): void {
    this.tourService.getAll().subscribe(tours => this.tours = tours);
  }

  onSearchChange(): void {
    this.searchSubject.next(this.searchQuery);
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  selectTour(tour: Tour): void {
    this.selectedTour = tour;
    this.tourLogs = [];
    this.tourStats = null;
    this.showStats = false;
    this.sidebarOpen = false;

    this.tourLogService.getAll(tour.id).subscribe(logs => this.tourLogs = logs);

    this.statisticsService.getTourStatistics(tour.id).subscribe({
      next: stats => this.tourStats = stats,
      error: () => this.tourStats = null
    });
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
    const editedId = this.editingTour?.id ?? this.selectedTour?.id ?? null;
    this.tourService.getAll().subscribe(tours => {
      this.tours = tours;
      // Re-point selectedTour at the freshly loaded object so the detail view
      // immediately shows the updated distance/time without a manual reload.
      if (editedId) {
        const updated = tours.find(t => t.id === editedId);
        if (updated) this.selectedTour = updated;
      }
    });
  }

  deleteTour(id: number): void {
    if (!confirm('Delete this tour?')) return;
    this.tourService.delete(id).subscribe(() => {
      this.loadTours();
      if (this.selectedTour?.id === id) {
        this.selectedTour = null;
        this.tourStats = null;
      }
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

  onExport(): void {
    this.importExportService.exportAllTours().subscribe({
      next: (data) => {
        const blob = new Blob([data], { type: 'application/json' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'tours-export.json';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => alert('Export failed.')
    });
  }

  onImport(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.importError = '';
    this.importSuccess = '';

    this.importExportService.importTours(file).subscribe({
      next: (res: any) => {
        this.importSuccess = `Imported ${res.importedCount} tours successfully!`;
        this.loadTours();
        input.value = '';
      },
      error: () => {
        this.importError = 'Import failed. Make sure the file is a valid JSON export.';
        input.value = '';
      }
    });
  }

  triggerImport(): void {
    document.getElementById('import-file-input')?.click();
  }

  openStats(): void {
    this.showStats = true;
    this.statsLoading = true;
    this.userStats = null;
    this.statisticsService.getUserStatistics().subscribe({
      next: stats => {
        this.userStats = stats;
        this.statsLoading = false;
      },
      error: () => {
        this.statsLoading = false;
      }
    });
  }

  closeStats(): void {
    this.showStats = false;
  }

  get transportTypeEntries(): { type: string; count: number; avgDistance: number }[] {
    if (!this.userStats?.transportTypeStats) return [];
    const counts = this.userStats.transportTypeStats.toursByTransportType;
    const avgDist = this.userStats.transportTypeStats.avgDistanceByTransportType;
    return Object.keys(counts).map(type => ({
      type,
      count: counts[type],
      avgDistance: avgDist[type] ?? 0
    }));
  }

  formatTime(minutes: number): string {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  getPopularityLabel(popularity: number | undefined): string {
    if (popularity === undefined || popularity === null) return 'N/A';
    if (popularity === 0) return 'New';
    if (popularity <= 2) return 'Low';
    if (popularity <= 5) return 'Medium';
    return 'High';
  }

  getChildFriendlinessLabel(value: number | undefined): string {
    if (value === undefined || value === null) return 'N/A';
    if (value >= 7) return 'Very friendly';
    if (value >= 4) return 'Moderate';
    return 'Challenging';
  }
}
