import { Shield, LogOut, LayoutDashboard, PlusCircle, Search } from "lucide-react";
import { NavLink, Outlet } from "react-router-dom";
import { useAuthStore } from "@/store/authStore";
import { useLogout } from "@/hooks/useAuth";

const navItems = [
  { to: "/", icon: LayoutDashboard, label: "Dashboard", end: true },
  { to: "/submit", icon: PlusCircle, label: "Submit Trade", end: false },
  { to: "/lookup", icon: Search, label: "Trade Lookup", end: false },
];

const Layout = () => {
  const accountId = useAuthStore((s) => s.accountId);
  const logout = useLogout();

  return (
    <div className="flex flex-col h-screen bg-zinc-950">
      {/* Navbar */}
      <header className="h-14 bg-zinc-900 border-b border-zinc-800 flex items-center justify-between px-4 shrink-0">
        <div className="flex items-center gap-2">
          <Shield className="text-violet-500" size={20} />
          <span className="text-zinc-100 font-semibold text-sm">SentinelTrade</span>
        </div>
        <div className="flex items-center gap-3">
          <span className="text-zinc-400 text-sm">{accountId}</span>
          <button
            onClick={logout}
            className="text-zinc-400 hover:text-zinc-100 transition-colors"
            aria-label="Sign out"
          >
            <LogOut size={16} />
          </button>
        </div>
      </header>

      <div className="flex flex-1 overflow-hidden">
        {/* Sidebar */}
        <nav className="w-52 bg-zinc-900 border-r border-zinc-800 flex flex-col gap-1 p-2 shrink-0">
          {navItems.map(({ to, icon: Icon, label, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                [
                  "flex items-center gap-3 px-3 py-2 text-sm rounded-md transition-colors",
                  isActive
                    ? "bg-zinc-800 text-zinc-100"
                    : "text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800/50",
                ].join(" ")
              }
            >
              <Icon size={16} />
              {label}
            </NavLink>
          ))}
        </nav>

        {/* Main content */}
        <main className="flex-1 overflow-auto bg-zinc-950 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default Layout;
