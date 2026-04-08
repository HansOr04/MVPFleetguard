import React from 'react';

interface SectionHeaderProps {
	icon: string;
	title: string;
	subtitle?: string;
}

export const SectionHeader: React.FC<SectionHeaderProps> = ({ icon, title, subtitle }) => {
	return (
		<div className="flex items-center gap-3 mb-6">
			<span className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center text-secondary-container">
				<span
					className="material-symbols-outlined text-sm"
					style={{ fontVariationSettings: "'FILL' 1" }}
				>
					{icon}
				</span>
			</span>
			<div>
				<h3 className="text-xl font-bold text-primary">{title}</h3>
				{subtitle && (
					<p className="text-xs text-on-surface-variant font-medium mt-0.5">{subtitle}</p>
				)}
			</div>
		</div>
	);
};