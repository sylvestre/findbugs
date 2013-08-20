/*
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2006, University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.umd.cs.findbugs.ba;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;

import edu.umd.cs.findbugs.AnalysisCacheToRepositoryAdapter;
import edu.umd.cs.findbugs.ba.ch.Subtypes2;
import edu.umd.cs.findbugs.ba.jsr305.DirectlyRelevantTypeQualifiersDatabase;
import edu.umd.cs.findbugs.ba.npe.IsNullValueAnalysisFeatures;
import edu.umd.cs.findbugs.ba.npe.ParameterNullnessPropertyDatabase;
import edu.umd.cs.findbugs.ba.npe.ReturnValueNullnessPropertyDatabase;
import edu.umd.cs.findbugs.ba.npe.TypeQualifierNullnessAnnotationDatabase;
import edu.umd.cs.findbugs.ba.type.FieldStoreTypeDatabase;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;
import edu.umd.cs.findbugs.classfile.analysis.ClassInfo;
import edu.umd.cs.findbugs.classfile.analysis.MethodInfo;
import edu.umd.cs.findbugs.internalAnnotations.DottedClassName;
import edu.umd.cs.findbugs.util.ClassName;

/**
 * An AnalysisContext implementation that uses the IAnalysisCache. This class
 * must only be used by FindBugs2, not the original FindBugs driver.
 *
 * @author David Hovemeyer
 */
public class AnalysisCacheToAnalysisContextAdapter extends AnalysisContext {

    static class DelegatingRepositoryLookupFailureCallback implements RepositoryLookupFailureCallback {

        /*
         * (non-Javadoc)
         *
         * @see
         * edu.umd.cs.findbugs.classfile.IErrorLogger#logError(java.lang.String)
         */
        public void logError(String message) {
            Global.getAnalysisCache().getErrorLogger().logError(message);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * edu.umd.cs.findbugs.classfile.IErrorLogger#logError(java.lang.String,
         * java.lang.Throwable)
         */
        public void logError(String message, Throwable e) {
            Global.getAnalysisCache().getErrorLogger().logError(message, e);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * edu.umd.cs.findbugs.classfile.IErrorLogger#reportMissingClass(java
         * .lang.ClassNotFoundException)
         */
        public void reportMissingClass(ClassNotFoundException ex) {
            Global.getAnalysisCache().getErrorLogger().reportMissingClass(ex);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * edu.umd.cs.findbugs.classfile.IErrorLogger#reportMissingClass(edu
         * .umd.cs.findbugs.classfile.ClassDescriptor)
         */
        public void reportMissingClass(ClassDescriptor classDescriptor) {
            Global.getAnalysisCache().getErrorLogger().reportMissingClass(classDescriptor);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * edu.umd.cs.findbugs.classfile.IErrorLogger#reportSkippedAnalysis(
         * edu.umd.cs.findbugs.classfile.MethodDescriptor)
         */
        public void reportSkippedAnalysis(MethodDescriptor method) {
            Global.getAnalysisCache().getErrorLogger().reportSkippedAnalysis(method);
        }

    }

    private RepositoryLookupFailureCallback lookupFailureCallback;

    /**
     * Constructor.
     */
    public AnalysisCacheToAnalysisContextAdapter() {
        this.lookupFailureCallback = new DelegatingRepositoryLookupFailureCallback();
    }

    // /* (non-Javadoc)
    // * @see
    // edu.umd.cs.findbugs.ba.AnalysisContext#addApplicationClassToRepository(org.apache.bcel.classfile.JavaClass)
    // */
    // @Override
    // public void addApplicationClassToRepository(JavaClass appClass) {
    // throw new UnsupportedOperationException();
    // }

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.umd.cs.findbugs.ba.AnalysisContext#addClasspathEntry(java.lang.String
     * )
     */
    @Override
    public void addClasspathEntry(String url) throws IOException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#clearClassContextCache()
     */
    @Override
    public void clearClassContextCache() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#clearRepository()
     */
    @Override
    public void clearRepository() {
        // Set the backing store for the BCEL Repository to
        // be the AnalysisCache.
        Repository.setRepository(new AnalysisCacheToRepositoryAdapter());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.umd.cs.findbugs.ba.AnalysisContext#getAnnotationRetentionDatabase()
     */
    @Override
    public AnnotationRetentionDatabase getAnnotationRetentionDatabase() {
        return getDatabase(AnnotationRetentionDatabase.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.umd.cs.findbugs.ba.AnalysisContext#getCheckReturnAnnotationDatabase()
     */
    @Override
    public CheckReturnAnnotationDatabase getCheckReturnAnnotationDatabase() {
        return getDatabase(CheckReturnAnnotationDatabase.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.umd.cs.findbugs.ba.AnalysisContext#getClassContext(org.apache.bcel
     * .classfile.JavaClass)
     */
    @Override
    public ClassContext getClassContext(JavaClass javaClass) {
        // This is a bit silly since we're doing an unnecessary
        // ClassDescriptor->JavaClass lookup.
        // However, we can be assured that it will succeed.

        ClassDescriptor classDescriptor = DescriptorFactory.instance().getClassDescriptor(
                ClassName.toSlashedClassName(javaClass.getClassName()));

        try {
            return Global.getAnalysisCache().getClassAnalysis(ClassContext.class, classDescriptor);
        } catch (CheckedAnalysisException e) {
            IllegalStateException ise = new IllegalStateException("Could not get ClassContext for JavaClass");
            ise.initCause(e);
            throw ise;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#getClassContextStats()
     */
    @Override
    public String getClassContextStats() {
        return "<unknown ClassContext stats>";
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#getFieldStoreTypeDatabase()
     */
    @Override
    public FieldStoreTypeDatabase getFieldStoreTypeDatabase() {
        return getDatabase(FieldStoreTypeDatabase.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#getJCIPAnnotationDatabase()
     */
    @Override
    public JCIPAnnotationDatabase getJCIPAnnotationDatabase() {
        return getDatabase(JCIPAnnotationDatabase.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#getLookupFailureCallback()
     */
    @Override
    public RepositoryLookupFailureCallback getLookupFailureCallback() {
        return lookupFailureCallback;
    }

    private TypeQualifierNullnessAnnotationDatabase tqNullnessDatabase;

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.umd.cs.findbugs.ba.AnalysisContext#getNullnessAnnotationDatabase()
     */
    @Override
    public INullnessAnnotationDatabase getNullnessAnnotationDatabase() {
        if (IsNullValueAnalysisFeatures.USE_TYPE_QUALIFIERS) {
            if (tqNullnessDatabase == null) {
                tqNullnessDatabase = new TypeQualifierNullnessAnnotationDatabase();
            }
            return tqNullnessDatabase;
        } else {
            return getDatabase(NullnessAnnotationDatabase.class);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#getSourceFinder()
     */
    @Override
    public SourceFinder getSourceFinder() {
        return project.getSourceFinder();
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#getSourceInfoMap()
     */
    @Override
    public SourceInfoMap getSourceInfoMap() {
        return getDatabase(SourceInfoMap.class);
    }

    // /* (non-Javadoc)
    // * @see edu.umd.cs.findbugs.ba.AnalysisContext#getSubtypes()
    // */
    // @Override
    // public Subtypes getSubtypes() {
    // if (Subtypes.DO_NOT_USE) {
    // throw new IllegalArgumentException();
    // }
    // return getDatabase(Subtypes.class);
    // }

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.umd.cs.findbugs.ba.AnalysisContext#getUnconditionalDerefParamDatabase
     * ()
     */
    @Override
    public ParameterNullnessPropertyDatabase getUnconditionalDerefParamDatabase() {
        return getDatabase(ParameterNullnessPropertyDatabase.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#initDatabases()
     */
    @Override
    public void initDatabases() {
        // Databases are created on-demand - don't need to explicitly create
        // them
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#lookupClass(java.lang.String)
     */
    @Override
    public JavaClass lookupClass(@Nonnull @DottedClassName String className) throws ClassNotFoundException {
        try {
            if (className.length() == 0)
                throw new IllegalArgumentException("Class name is empty");
            if (!ClassName.isValidClassName(className)) {
                throw new ClassNotFoundException("Invalid class name: " + className);
            }
            return Global.getAnalysisCache().getClassAnalysis(JavaClass.class,
                    DescriptorFactory.instance().getClassDescriptor(ClassName.toSlashedClassName(className)));
        } catch (CheckedAnalysisException e) {
            throw new ClassNotFoundException("Class not found: " + className, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#getInnerClassAccessMap()
     */
    @Override
    public InnerClassAccessMap getInnerClassAccessMap() {
        return getDatabase(InnerClassAccessMap.class);
    }

    /**
     * Helper method to get a database without having to worry about a
     * CheckedAnalysisException.
     *
     * @param cls
     *            Class of the database to get
     * @return the database
     */
    private <E> E getDatabase(Class<E> cls) {
        return Global.getAnalysisCache().getDatabase(cls);
    }

    /**
     * Set the collection of class descriptors identifying all application
     * classes.
     *
     * @param appClassCollection
     *            List of ClassDescriptors identifying application classes
     */
    public void setAppClassList(List<ClassDescriptor> appClassCollection) {

        // FIXME: we really should drive the progress callback here
        HashSet<ClassDescriptor> appSet = new HashSet<ClassDescriptor>(appClassCollection);

        Collection<ClassDescriptor> allClassDescriptors = new ArrayList<ClassDescriptor>(DescriptorFactory.instance()
                .getAllClassDescriptors());
        for (ClassDescriptor appClass : allClassDescriptors)
            try {
                XClass xclass = currentXFactory().getXClass(appClass);

                if (xclass == null)
                    continue;

                // Add the application class to the database
                if (appSet.contains(appClass))
                    getSubtypes2().addApplicationClass(xclass);
                else if (xclass instanceof ClassInfo)
                    getSubtypes2().addClass(xclass);

            } catch (Exception e) {
                AnalysisContext.logError("Unable to get XClass for " + appClass, e);
            }

        if (true && Subtypes2.DEBUG) {
            System.out.println(getSubtypes2().getGraph().getNumVertices() + " vertices in inheritance graph");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#updateDatabases(int)
     */
    @Override
    public void updateDatabases(int pass) {
        if (pass == 0) {
            getCheckReturnAnnotationDatabase().loadAuxiliaryAnnotations();
            getNullnessAnnotationDatabase().loadAuxiliaryAnnotations();
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.umd.cs.findbugs.ba.AnalysisContext#getReturnValueNullnessPropertyDatabase
     * ()
     */
    @Override
    public ReturnValueNullnessPropertyDatabase getReturnValueNullnessPropertyDatabase() {
        return getDatabase(ReturnValueNullnessPropertyDatabase.class);
    }

    // private Subtypes2 subtypes2;

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#getSubtypes2()
     */
    @Override
    public Subtypes2 getSubtypes2() {
        return Global.getAnalysisCache().getDatabase(Subtypes2.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.umd.cs.findbugs.ba.AnalysisContext#
     * getDirectlyRelevantTypeQualifiersDatabase()
     */
    @Override
    public DirectlyRelevantTypeQualifiersDatabase getDirectlyRelevantTypeQualifiersDatabase() {
        return Global.getAnalysisCache().getDatabase(DirectlyRelevantTypeQualifiersDatabase.class);
    }

    @Override
    public @CheckForNull
    XMethod getBridgeTo(MethodInfo m) {
        return bridgeTo.get(m);
    }

    @Override
    public @CheckForNull
    XMethod getBridgeFrom(MethodInfo m) {
        return bridgeFrom.get(m);
    }

    @Override
    public void setBridgeMethod(MethodInfo from, MethodInfo to) {
        bridgeTo.put(from, to);
        bridgeFrom.put(to, from);
    }

    final Map<MethodInfo, MethodInfo> bridgeTo = new IdentityHashMap<MethodInfo, MethodInfo>();

    final Map<MethodInfo, MethodInfo> bridgeFrom = new IdentityHashMap<MethodInfo, MethodInfo>();

}
