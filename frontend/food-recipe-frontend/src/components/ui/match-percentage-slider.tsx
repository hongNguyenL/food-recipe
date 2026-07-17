interface MatchPercentageSliderProps {
  value: number
  onChange: (value: number) => void
}

export function MatchPercentageSlider({ value, onChange }: MatchPercentageSliderProps) {
  return (
    <div className="space-y-2 min-w-[200px] flex-1">
      <label htmlFor="min-match-slider" className="text-sm font-medium">
        Minimum Match: <span className="tabular-nums">{value}%</span>
      </label>
      <input
        id="min-match-slider"
        type="range"
        min={0}
        max={100}
        step={1}
        value={value}
        onChange={(e) => onChange(Number(e.target.value))}
        className="w-full accent-[var(--primary)] cursor-pointer"
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={value}
        aria-label={`Minimum match percentage: ${value} percent`}
      />
      <div className="flex justify-between text-xs text-[var(--muted-foreground)]">
        <span>0%</span>
        <span>100%</span>
      </div>
    </div>
  )
}
