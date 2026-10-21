import Navbar from "../components/landing/Navbar.jsx";
import Hero from "../components/landing/Hero.jsx";
import WhyDonate from "../components/landing/WhyDonate.jsx";
import Benefits from "../components/landing/Benefits.jsx";
import Services from "../components/landing/Services.jsx";
import HowItWorks from "../components/landing/HowItWorks.jsx";
import BloodGroups from "../components/landing/BloodGroups.jsx";
import FAQ from "../components/landing/FAQ.jsx";
import Testimonials from "../components/landing/Testimonials.jsx";
import CTA from "../components/landing/CTA.jsx";
import Footer from "../components/landing/Footer.jsx";

// Landing page complete — Steps 2, 3 & 4 combined.
export default function LandingPage() {
  return (
    <div className="min-h-screen">
      <Navbar />
      <Hero />
      <WhyDonate />
      <Benefits />
      <Services />
      <HowItWorks />
      <BloodGroups />
      <FAQ />
      <Testimonials />
      <CTA />
      <Footer />
    </div>
  );
}
