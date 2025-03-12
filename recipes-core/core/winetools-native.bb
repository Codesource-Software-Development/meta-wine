inherit autotools
inherit native

AUTORECONF = "no"
LICENSE = "LGPLv2"
# The winegcc patch fixes how winegcc calls the cross compiler
# It does this by removing the -b switch from calls to winegcc
# Making winegcc fall back on the default which is the one the CC environment variable points to
# during the configure stage. Since bitbake adds all the necessary switches for the cross compiler to
# find libs to CC this fixes the build w.r.t winegcc
 
SRC_URI = "git://gitlab.winehq.org/wine/wine.git;protocol=https;branch=master file://patch_winegcc.patch"
SRCREV = "5b3306a0d0aff221195ecc29872f25ed082eedd0"
SRC_URI[sha256sum] = "3384810c536cfab020ab4af809bbb677f59bc46ec528c70f7ca27d0c8a42b397"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=f52e77babf61480e86e8a5bbe3fdf1e8"
S = "${WORKDIR}/git"

DEPENDS = "autoconf automake libtool pkgconfig"

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

#BBCLASSEXTEND = "native nativesdk"

DEPENDS += "flex-native bison-native"

EXTRA_OECONF += " --without-x --without-freetype "

# Conditionally add --enable-win64 based on native architecture
# This is needed to make sure the configure script won't tell gcc to generate 32 bit applications
# during the configure stage and mess up any executables being built and needed during compilation
# such as makedep
# Still need to figure out what the effect is on the winetools and if they behave differently when
# build for 32 bit or 64 bit or arm or x86(32/64)

python __anonymous() {
    import re
    host_arch = d.getVar('HOST_ARCH')
    if re.match(r'x86_64|amd64', host_arch):
        d.appendVar('EXTRA_OECONF', ' --enable-win64 ')
}

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${bindir}/nls
	cp -R ${WORKDIR}/build/tools ${D}${bindir}
	cp -R ${S}/nls/*.nls ${D}${bindir}/nls
}

