export const DONATION_COOLDOWN_DAYS = 120;

// Whole days remaining until this donor is eligible to donate again
// (0 if they've never donated, or the cooldown has already passed).
export const getDaysUntilEligible = (lastDonationDate) => {
  if (!lastDonationDate) return 0;
  const next = new Date(lastDonationDate);
  next.setDate(next.getDate() + DONATION_COOLDOWN_DAYS);
  const diffMs = next.getTime() - Date.now();
  return diffMs > 0 ? Math.ceil(diffMs / (1000 * 60 * 60 * 24)) : 0;
};

export const getNextEligibleDateLabel = (lastDonationDate) => {
  if (!lastDonationDate) return null;
  const next = new Date(lastDonationDate);
  next.setDate(next.getDate() + DONATION_COOLDOWN_DAYS);
  return next.toLocaleDateString();
};

export const isInCooldown = (lastDonationDate) => getDaysUntilEligible(lastDonationDate) > 0;
