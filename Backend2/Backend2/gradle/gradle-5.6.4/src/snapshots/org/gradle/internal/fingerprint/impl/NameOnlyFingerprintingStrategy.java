/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.fingerprint.impl;

import com.google.common.collect.ImmutableMap;
import org.gradle.internal.fingerprint.FileSystemLocationFingerprint;
import org.gradle.internal.fingerprint.FingerprintHashingStrategy;
import org.gradle.internal.snapshot.DirectorySnapshot;
import org.gradle.internal.snapshot.FileSystemLocationSnapshot;
import org.gradle.internal.snapshot.FileSystemSnapshot;
import org.gradle.internal.snapshot.FileSystemSnapshotVisitor;

import java.util.HashSet;
import java.util.Map;

/**
 * Fingerprint files normalizing the path to the file name.
 *
 * File names for root directories are ignored.
 */
public class NameOnlyFingerprintingStrategy extends AbstractFingerprintingStrategy {

    public static final NameOnlyFingerprintingStrategy INSTANCE = new NameOnlyFingerprintingStrategy();
    public static final String IDENTIFIER = "NAME_ONLY";

    private NameOnlyFingerprintingStrategy() {
        super(IDENTIFIER);
    }

    @Override
    public String normalizePath(FileSystemLocationSnapshot snapshot) {
        return snapshot.getName();
    }

    @Override
    public Map<String, FileSystemLocationFingerprint> collectFingerprints(Iterable<? extends FileSystemSnapshot> roots) {
        final ImmutableMap.Builder<String, FileSystemLocationFingerprint> builder = ImmutableMap.builder();
        final HashSet<String> processedEntries = new HashSet<String>();
        for (FileSystemSnapshot root : roots) {
            root.accept(new FileSystemSnapshotVisitor() {
                private boolean root = true;

                @Override
                public boolean preVisitDirectory(DirectorySnapshot directorySnapshot) {
                    String absolutePath = directorySnapshot.getAbsolutePath();
                    if (processedEntries.add(absolutePath)) {
                        FileSystemLocationFingerprint fingerprint = isRoot() ? IgnoredPathFileSystemLocationFingerprint.DIRECTORY : new DefaultFileSystemLocationFingerprint(directorySnapshot.getName(), directorySnapshot);
                        builder.put(absolutePath, fingerprint);
                    }
                    root = false;
                    return true;
                }

                @Override
                public void visitFile(FileSystemLocationSnapshot fileSnapshot) {
                    String absolutePath = fileSnapshot.getAbsolutePath();
                    if (processedEntries.add(absolutePath)) {
                        builder.put(
                            absolutePath,
                            new DefaultFileSystemLocationFingerprint(fileSnapshot.getName(), fileSnapshot));
                    }
                }

                private boolean isRoot() {
                    return root;
                }

                @Override
                public void postVisitDirectory(DirectorySnapshot directorySnapshot) {
                }
            });
        }
        return builder.build();
    }

    @Override
    public FingerprintHashingStrategy getHashingStrategy() {
        return FingerprintHashingStrategy.SORT;
    }
}
