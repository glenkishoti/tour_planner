import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TourLogService } from '../../services/tour-log.service';
import { TourLog, TourLogRequest } from '../../models/tour-log';

@Component({
  selector: 'app-tour-log-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './tour-log-form.component.html',
  styleUrl: './tour-log-form.component.scss'
})
export class TourLogFormComponent implements OnInit {
  @Input() tourId!: number;
  @Input() log: TourLog | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  dateTime = '';
  comment = '';
  difficulty: number = 1;
  totalDistance: number | null = null;
  totalTimeMinutes: number | null = null;
  rating: number = 1;
  errorMessage = '';

  constructor(private tourLogService: TourLogService) {}

  ngOnInit(): void {
    if (this.log) {
      this.dateTime = this.log.dateTime.substring(0, 16);
      this.comment = this.log.comment;
      this.difficulty = this.log.difficulty;
      this.totalDistance = this.log.totalDistance;
      this.totalTimeMinutes = this.log.totalTimeMinutes;
      this.rating = this.log.rating;
    } else {
      this.dateTime = new Date().toISOString().substring(0, 16);
    }
  }

  onSave(): void {
    if (!this.comment || !this.totalDistance || !this.totalTimeMinutes) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    const request: TourLogRequest = {
      dateTime: new Date(this.dateTime).toISOString(),
      comment: this.comment,
      difficulty: this.difficulty,
      totalDistance: this.totalDistance,
      totalTimeMinutes: this.totalTimeMinutes,
      rating: this.rating
    };

    const action = this.log
      ? this.tourLogService.update(this.tourId, this.log.id, request)
      : this.tourLogService.create(this.tourId, request);

    action.subscribe({
      next: () => this.saved.emit(),
      error: () => this.errorMessage = 'Failed to save log.'
    });
  }
}
