inherit autotools

AUTORECONF = "no"
LICENSE = "LGPLv2" 
SRC_URI = "git://gitlab.winehq.org/wine/wine.git;protocol=https;branch=master"
SRCREV = "5b3306a0d0aff221195ecc29872f25ed082eedd0"
SRC_URI[sha256sum] = "3384810c536cfab020ab4af809bbb677f59bc46ec528c70f7ca27d0c8a42b397"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=f52e77babf61480e86e8a5bbe3fdf1e8"
S = "${WORKDIR}/git"

DEPENDS = "autoconf automake libtool pkgconfig"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11', '', d)}"

AUTOTOOLS_SCRIPT_PATH ?= "${S}"
CONFIGURE_SCRIPT ?= "${AUTOTOOLS_SCRIPT_PATH}/configure"

do_configure() {
        if [ -e ${CONFIGURE_SCRIPT} ]; then
                oe_runconf
        else
                bbnote "nothing to configure"
        fi
}

autotools_aclocals() {
}

# TODO figure out how to build with freetype and then remove --without-freetype
EXTRA_OECONF += " --with-wine-tools=${STAGING_BINDIR_NATIVE} --without-freetype"
EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', '--without-x', d)}"
EXTRA_OEMAKE += "WITH_NLS=./nls"

DEPENDS += "winetools-native flex-native bison-native"


