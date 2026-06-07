import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScoutingPlayerListComponent } from './scouting-player-list.component';

describe('ScoutingPlayerListComponent', () => {
  let component: ScoutingPlayerListComponent;
  let fixture: ComponentFixture<ScoutingPlayerListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ScoutingPlayerListComponent]
    });
    fixture = TestBed.createComponent(ScoutingPlayerListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
