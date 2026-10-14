import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ScoreGauge } from '../components/ScoreGauge';
import { SkillBadge } from '../components/SkillBadge';

describe('Frontend Component Tests', () => {
  it('ScoreGauge displays percentage and correct match category', () => {
    const { rerender } = render(<ScoreGauge score={85} />);
    expect(screen.getByText('85%')).toBeInTheDocument();
    expect(screen.getByText('Strong Match')).toBeInTheDocument();

    rerender(<ScoreGauge score={40} />);
    expect(screen.getByText('40%')).toBeInTheDocument();
    expect(screen.getByText('Low Match')).toBeInTheDocument();
  });

  it('SkillBadge displays skill name and category correctly', () => {
    render(
      <SkillBadge
        name="Java"
        category="Language"
        status="MATCHED"
      />
    );
    expect(screen.getByText('Java')).toBeInTheDocument();
    expect(screen.getByText('(Language)')).toBeInTheDocument();
  });

  it('SkillBadge renders missing preferred skill badge', () => {
    render(
      <SkillBadge
        name="Kubernetes"
        category="DevOps"
        status="MISSING"
        importance="PREFERRED"
      />
    );
    expect(screen.getByText('Kubernetes')).toBeInTheDocument();
    expect(screen.getByText('Nice-to-have')).toBeInTheDocument();
  });
});
