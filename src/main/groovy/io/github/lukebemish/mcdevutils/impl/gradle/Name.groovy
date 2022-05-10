package io.github.lukebemish.mcdevutils.impl.gradle

class Name {

    private final String[] pieces;

    Name(String original) {
        if (original.startsWith('L') && original.endsWith(';')) {
            this.pieces = original.substring(1,original.length()-1).split('/')
        } else if (original.contains('/')) {
            this.pieces = original.split('/')
        } else if (original.contains('.')) {
            this.pieces = original.split('\\.')
        } else {
            this.pieces = new String[] {}
        }
    }

    @Override
    boolean equals(Object obj) {
        if (obj instanceof Name) {
            return Arrays.equals(pieces,obj.pieces)
        }
        return false
    }

    int hashCode() {
        return Arrays.hashCode(pieces)
    }

    String getDescriptor() {
        return 'L'+String.join('/',pieces)+';'
    }

    boolean isPackage() {
        return (pieces.length>1 && pieces[pieces.length-1]=='package-info')
    }

    Name getParent() {
        if (pieces.length > 1) {
            String[] newPieces = new String[pieces.length - 1]
            for (int i = 0; i < pieces.length - 1; i++) {
                newPieces[i] = pieces[i]
            }
            return new Name(String.join('.', newPieces))
        }
        return null
    }
}
