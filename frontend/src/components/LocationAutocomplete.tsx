import { useState, useEffect, useRef } from 'react';

interface LocationResult {
    display_name: string;
    lat: string;
    lon: string;
}

interface LocationAutocompleteProps {
    label: string;
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
    required?: boolean;
}

const LocationAutocomplete = ({ label, value, onChange, placeholder, required }: LocationAutocompleteProps) => {
    const [isOpen, setIsOpen] = useState(false);
    const [results, setResults] = useState<LocationResult[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const wrapperRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (value.length < 3) {
            setResults([]);
            setIsOpen(false);
            return;
        }

        const timeoutId = setTimeout(async () => {
            setLoading(true);
            setError('');

            try {
                const response = await fetch(
                    `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(value)}&limit=5`,
                    {
                        headers: {
                            'User-Agent': 'RideMate App (https://github.com/ridemate)',
                        },
                    }
                );

                if (!response.ok) {
                    throw new Error('Failed to fetch locations');
                }

                const data = await response.json();
                setResults(data);
                setIsOpen(data.length > 0);
            } catch (err) {
                setError('Failed to load locations');
                setResults([]);
                setIsOpen(false);
            } finally {
                setLoading(false);
            }
        }, 500);

        return () => clearTimeout(timeoutId);
    }, [value]);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const handleSelect = (result: LocationResult) => {
        onChange(result.display_name);
        setIsOpen(false);
        setResults([]);
    };

    return (
        <div ref={wrapperRef} className="relative">
            <label className="block text-sm font-medium text-gray-300 mb-2">{label}</label>
            <div className="relative">
                <input
                    type="text"
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    className="w-full px-4 py-3 bg-gray-900/50 border border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-white placeholder-gray-500 transition-all"
                    placeholder={placeholder || 'Type to search locations...'}
                    required={required}
                />
                {loading && (
                    <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
                        <div className="animate-spin h-5 w-5 border-2 border-indigo-500 border-t-transparent rounded-full"></div>
                    </div>
                )}
            </div>

            {error && (
                <div className="mt-1 text-xs text-red-400">{error}</div>
            )}

            {isOpen && results.length > 0 && (
                <div className="absolute z-50 w-full mt-1 bg-gray-800 border border-gray-600 rounded-lg shadow-lg max-h-60 overflow-y-auto">
                    {results.map((result, index) => (
                        <button
                            key={index}
                            type="button"
                            onClick={() => handleSelect(result)}
                            className="w-full text-left px-4 py-3 hover:bg-gray-700 transition-colors border-b border-gray-700 last:border-b-0 focus:bg-gray-700 focus:outline-none"
                        >
                            <div className="text-white text-sm">{result.display_name}</div>
                            <div className="text-gray-400 text-xs mt-1">
                                Lat: {parseFloat(result.lat).toFixed(4)}, Lon: {parseFloat(result.lon).toFixed(4)}
                            </div>
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
};

export default LocationAutocomplete;
