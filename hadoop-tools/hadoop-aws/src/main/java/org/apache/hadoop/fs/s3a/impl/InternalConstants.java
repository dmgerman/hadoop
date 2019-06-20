begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|impl
package|;
end_package

begin_comment
comment|/**  * Internal constants private only to the S3A codebase.  */
end_comment

begin_class
DECL|class|InternalConstants
specifier|public
specifier|final
class|class
name|InternalConstants
block|{
DECL|method|InternalConstants ()
specifier|private
name|InternalConstants
parameter_list|()
block|{   }
comment|/**    * This is an arbitrary value: {@value}.    * It declares how many parallel copy operations    * in a single rename can be queued before the operation pauses    * and awaits completion.    * A very large value wouldn't just starve other threads from    * performing work, there's a risk that the S3 store itself would    * throttle operations (which all go to the same shard).    * It is not currently configurable just to avoid people choosing values    * which work on a microbenchmark (single rename, no other work, ...)    * but don't scale well to execution in a large process against a common    * store, all while separate processes are working with the same shard    * of storage.    *    * It should be a factor of {@link #MAX_ENTRIES_TO_DELETE} so that    * all copies will have finished before deletion is contemplated.    * (There's always a block for that, it just makes more sense to    * perform the bulk delete after another block of copies have completed).    */
DECL|field|RENAME_PARALLEL_LIMIT
specifier|public
specifier|static
specifier|final
name|int
name|RENAME_PARALLEL_LIMIT
init|=
literal|10
decl_stmt|;
comment|/**    * The maximum number of entries that can be deleted in any bulk delete    * call to S3: {@value}.    */
DECL|field|MAX_ENTRIES_TO_DELETE
specifier|public
specifier|static
specifier|final
name|int
name|MAX_ENTRIES_TO_DELETE
init|=
literal|1000
decl_stmt|;
comment|/**    * Default blocksize as used in blocksize and FS status queries: {@value}.    */
DECL|field|DEFAULT_BLOCKSIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BLOCKSIZE
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
block|}
end_class

end_unit

