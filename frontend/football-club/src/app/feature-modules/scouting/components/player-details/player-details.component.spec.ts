import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayerDetailsComponent } from './view-player.component';

describe('PlayerDetailsComponent', () => {
  let component: PlayerDetailsComponent;
  let fixture: ComponentFixture<PlayerDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlayerDetailsComponent]
    });
    fixture = TestBed.createComponent(PlayerDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
