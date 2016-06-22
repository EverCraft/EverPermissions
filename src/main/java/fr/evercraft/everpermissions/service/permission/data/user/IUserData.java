package fr.evercraft.everpermissions.service.permission.data.user;

import org.spongepowered.api.util.Tristate;

public interface IUserData {
    
    public void load(EUserData subject);
    
    /*
     * Permissions
     */

    public boolean setPermission(final String subject, final String world, final String  permission, final Tristate value, final boolean insert);

    public boolean clearPermissions(final String subject, final String world);
    
    public boolean clearPermissions(final String subject);
    
    /*
     * Options
     */
    
    public boolean setOption(final String subject, final String world, final String type, final String name, final boolean insert);

    public boolean clearOptions(final String subject, final String world);

    public boolean clearOptions(final String subject);

    /*
     * Groups
     */
    
    public boolean addParent(final String subject, final String world, final String parent);

    public boolean removeParent(final String subject, final String world, final String parent);

    public boolean clearParents(final String subject, final String world);

    public boolean clearParents(final String subject);
    
    /*
     * SubGroups
     */
    
    public boolean addSubParent(final String subject, final String world, final String parent);

    public boolean removeSubParent(final String subject, final String world, final String parent);
    
    public boolean clearSubParents(final String subject, final String world);

    public boolean clearSubParents(final String subject);
}
