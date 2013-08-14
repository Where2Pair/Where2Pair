package org.where2pair.grails

import org.apache.commons.lang.builder.HashCodeBuilder

class GormUserGormRole implements Serializable {

	GormUser gormUser
	GormRole gormRole

	boolean equals(other) {
		if (!(other instanceof GormUserGormRole)) {
			return false
		}

		other.gormUser?.id == gormUser?.id &&
			other.gormRole?.id == gormRole?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (gormUser) builder.append(gormUser.id)
		if (gormRole) builder.append(gormRole.id)
		builder.toHashCode()
	}

	static GormUserGormRole get(long gormUserId, long gormRoleId) {
		find 'from GormUserGormRole where gormUser.id=:gormUserId and gormRole.id=:gormRoleId',
			[gormUserId: gormUserId, gormRoleId: gormRoleId]
	}

	static GormUserGormRole create(GormUser gormUser, GormRole gormRole, boolean flush = false) {
		new GormUserGormRole(gormUser: gormUser, gormRole: gormRole).save(flush: flush, insert: true)
	}

	static boolean remove(GormUser gormUser, GormRole gormRole, boolean flush = false) {
		GormUserGormRole instance = GormUserGormRole.findByGormUserAndGormRole(gormUser, gormRole)
		if (!instance) {
			return false
		}

		instance.delete(flush: flush)
		true
	}

	static void removeAll(GormUser gormUser) {
		executeUpdate 'DELETE FROM GormUserGormRole WHERE gormUser=:gormUser', [gormUser: gormUser]
	}

	static void removeAll(GormRole gormRole) {
		executeUpdate 'DELETE FROM GormUserGormRole WHERE gormRole=:gormRole', [gormRole: gormRole]
	}

	static mapping = {
		id composite: ['gormRole', 'gormUser']
		version false
	}
}
