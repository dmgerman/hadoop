begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
package|;
end_package

begin_comment
comment|/**  * Options for contract tests: keys for FS-specific values,  * defaults.  */
end_comment

begin_interface
DECL|interface|ContractOptions
specifier|public
interface|interface
name|ContractOptions
block|{
comment|/**    * name of the (optional) resource containing filesystem binding keys : {@value}    * If found, it it will be loaded    */
DECL|field|CONTRACT_OPTIONS_RESOURCE
name|String
name|CONTRACT_OPTIONS_RESOURCE
init|=
literal|"contract-test-options.xml"
decl_stmt|;
comment|/**    * Prefix for all contract keys in the configuration files    */
DECL|field|FS_CONTRACT_KEY
name|String
name|FS_CONTRACT_KEY
init|=
literal|"fs.contract."
decl_stmt|;
comment|/**    * Flag to indicate that a newly created file may overwrite a pre-existing    * directory.    * {@value}    */
DECL|field|CREATE_OVERWRITES_DIRECTORY
name|String
name|CREATE_OVERWRITES_DIRECTORY
init|=
literal|"create-overwrites-directory"
decl_stmt|;
comment|/**    * Flag to indicate that a newly created file is not made visible in the    * namespace immediately.  Instead, the file becomes visible at a later point    * in the file creation lifecycle, such as when the client closes it.    * {@value}    */
DECL|field|CREATE_VISIBILITY_DELAYED
name|String
name|CREATE_VISIBILITY_DELAYED
init|=
literal|"create-visibility-delayed"
decl_stmt|;
comment|/**    * Is a filesystem case sensitive.    * Some of the filesystems that say "no" here may mean    * that it varies from platform to platform -the localfs being the key    * example.    * {@value}    */
DECL|field|IS_CASE_SENSITIVE
name|String
name|IS_CASE_SENSITIVE
init|=
literal|"is-case-sensitive"
decl_stmt|;
comment|/**    * Blobstore flag. Implies it's not a real directory tree and    * consistency is below that which Hadoop expects    * {@value}    */
DECL|field|IS_BLOBSTORE
name|String
name|IS_BLOBSTORE
init|=
literal|"is-blobstore"
decl_stmt|;
comment|/**    * Flag to indicate that the FS can rename into directories that    * don't exist, creating them as needed.    * {@value}    */
DECL|field|RENAME_CREATES_DEST_DIRS
name|String
name|RENAME_CREATES_DEST_DIRS
init|=
literal|"rename-creates-dest-dirs"
decl_stmt|;
comment|/**    * Flag to indicate that the FS does not follow the rename contract -and    * instead only returns false on a failure.    * {@value}    */
DECL|field|RENAME_OVERWRITES_DEST
name|String
name|RENAME_OVERWRITES_DEST
init|=
literal|"rename-overwrites-dest"
decl_stmt|;
comment|/**    * Flag to indicate that the FS returns false if the destination exists    * {@value}    */
DECL|field|RENAME_RETURNS_FALSE_IF_DEST_EXISTS
name|String
name|RENAME_RETURNS_FALSE_IF_DEST_EXISTS
init|=
literal|"rename-returns-false-if-dest-exists"
decl_stmt|;
comment|/**    * Flag to indicate that the FS returns false on a rename    * if the source is missing    * {@value}    */
DECL|field|RENAME_RETURNS_FALSE_IF_SOURCE_MISSING
name|String
name|RENAME_RETURNS_FALSE_IF_SOURCE_MISSING
init|=
literal|"rename-returns-false-if-source-missing"
decl_stmt|;
comment|/**    * Flag to indicate that the FS remove dest first if it is an empty directory    * mean the FS honors POSIX rename behavior.    * {@value}    */
DECL|field|RENAME_REMOVE_DEST_IF_EMPTY_DIR
name|String
name|RENAME_REMOVE_DEST_IF_EMPTY_DIR
init|=
literal|"rename-remove-dest-if-empty-dir"
decl_stmt|;
comment|/**    * Flag to indicate that append is supported    * {@value}    */
DECL|field|SUPPORTS_APPEND
name|String
name|SUPPORTS_APPEND
init|=
literal|"supports-append"
decl_stmt|;
comment|/**    * Flag to indicate that setTimes is supported.    * {@value}    */
DECL|field|SUPPORTS_SETTIMES
name|String
name|SUPPORTS_SETTIMES
init|=
literal|"supports-settimes"
decl_stmt|;
comment|/**    * Flag to indicate that getFileStatus is supported.    * {@value}    */
DECL|field|SUPPORTS_GETFILESTATUS
name|String
name|SUPPORTS_GETFILESTATUS
init|=
literal|"supports-getfilestatus"
decl_stmt|;
comment|/**    * Flag to indicate that renames are atomic    * {@value}    */
DECL|field|SUPPORTS_ATOMIC_RENAME
name|String
name|SUPPORTS_ATOMIC_RENAME
init|=
literal|"supports-atomic-rename"
decl_stmt|;
comment|/**    * Flag to indicate that directory deletes are atomic    * {@value}    */
DECL|field|SUPPORTS_ATOMIC_DIRECTORY_DELETE
name|String
name|SUPPORTS_ATOMIC_DIRECTORY_DELETE
init|=
literal|"supports-atomic-directory-delete"
decl_stmt|;
comment|/**    * Does the FS support multiple block locations?    * {@value}    */
DECL|field|SUPPORTS_BLOCK_LOCALITY
name|String
name|SUPPORTS_BLOCK_LOCALITY
init|=
literal|"supports-block-locality"
decl_stmt|;
comment|/**    * Does the FS support the concat() operation?    * {@value}    */
DECL|field|SUPPORTS_CONCAT
name|String
name|SUPPORTS_CONCAT
init|=
literal|"supports-concat"
decl_stmt|;
comment|/**    * Is seeking supported at all?    * {@value}    */
DECL|field|SUPPORTS_SEEK
name|String
name|SUPPORTS_SEEK
init|=
literal|"supports-seek"
decl_stmt|;
comment|/**    * Is seeking past the EOF allowed?    * {@value}    */
DECL|field|REJECTS_SEEK_PAST_EOF
name|String
name|REJECTS_SEEK_PAST_EOF
init|=
literal|"rejects-seek-past-eof"
decl_stmt|;
comment|/**    * Is seeking on a closed file supported? Some filesystems only raise an    * exception later, when trying to read.    * {@value}    */
DECL|field|SUPPORTS_SEEK_ON_CLOSED_FILE
name|String
name|SUPPORTS_SEEK_ON_CLOSED_FILE
init|=
literal|"supports-seek-on-closed-file"
decl_stmt|;
comment|/**    * Is available() on a closed InputStream supported?    * {@value}    */
DECL|field|SUPPORTS_AVAILABLE_ON_CLOSED_FILE
name|String
name|SUPPORTS_AVAILABLE_ON_CLOSED_FILE
init|=
literal|"supports-available-on-closed-file"
decl_stmt|;
comment|/**    * Flag to indicate that this FS expects to throw the strictest    * exceptions it can, not generic IOEs, which, if returned,    * must be rejected.    * {@value}    */
DECL|field|SUPPORTS_STRICT_EXCEPTIONS
name|String
name|SUPPORTS_STRICT_EXCEPTIONS
init|=
literal|"supports-strict-exceptions"
decl_stmt|;
comment|/**    * Are unix permissions    * {@value}    */
DECL|field|SUPPORTS_UNIX_PERMISSIONS
name|String
name|SUPPORTS_UNIX_PERMISSIONS
init|=
literal|"supports-unix-permissions"
decl_stmt|;
comment|/**    * Is positioned readable supported? Supporting seek should be sufficient    * for this.    * {@value}    */
DECL|field|SUPPORTS_POSITIONED_READABLE
name|String
name|SUPPORTS_POSITIONED_READABLE
init|=
literal|"supports-positioned-readable"
decl_stmt|;
comment|/**    * Indicates that FS exposes durable references to files.    */
DECL|field|SUPPORTS_FILE_REFERENCE
name|String
name|SUPPORTS_FILE_REFERENCE
init|=
literal|"supports-file-reference"
decl_stmt|;
comment|/**    * Indicates that FS supports content checks on open.    */
DECL|field|SUPPORTS_CONTENT_CHECK
name|String
name|SUPPORTS_CONTENT_CHECK
init|=
literal|"supports-content-check"
decl_stmt|;
comment|/**    * Maximum path length    * {@value}    */
DECL|field|MAX_PATH_
name|String
name|MAX_PATH_
init|=
literal|"max-path"
decl_stmt|;
comment|/**    * Maximum filesize: 0 or -1 for no limit    * {@value}    */
DECL|field|MAX_FILESIZE
name|String
name|MAX_FILESIZE
init|=
literal|"max-filesize"
decl_stmt|;
comment|/**    * Flag to indicate that tests on the root directories of a filesystem/    * object store are permitted    * {@value}    */
DECL|field|TEST_ROOT_TESTS_ENABLED
name|String
name|TEST_ROOT_TESTS_ENABLED
init|=
literal|"test.root-tests-enabled"
decl_stmt|;
comment|/**    * Limit for #of random seeks to perform.    * Keep low for remote filesystems for faster tests    * {@value}    */
DECL|field|TEST_RANDOM_SEEK_COUNT
name|String
name|TEST_RANDOM_SEEK_COUNT
init|=
literal|"test.random-seek-count"
decl_stmt|;
block|}
end_interface

end_unit

