import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayerComparisonComponent } from './player-comparison.component';

describe('PlayerComparisonComponent', () => {
  let component: PlayerComparisonComponent;
  let fixture: ComponentFixture<PlayerComparisonComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlayerComparisonComponent]
    });
    fixture = TestBed.createComponent(PlayerComparisonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
