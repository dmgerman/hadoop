begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.filecache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|filecache
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|MRJobConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test the {@link DistributedCache} class.  */
end_comment

begin_class
DECL|class|TestDistributedCache
specifier|public
class|class
name|TestDistributedCache
block|{
comment|/**    * Test of addFileOnlyToClassPath method, of class DistributedCache.    */
annotation|@
name|Test
DECL|method|testAddFileToClassPath ()
specifier|public
name|void
name|testAddFileToClassPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// Test first with 2 args
try|try
block|{
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Accepted null archives argument"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///a"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.classpath.files property was not "
operator|+
literal|"set correctly"
argument_list|,
literal|"file:/a"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.cache.files property was not set "
operator|+
literal|"correctly"
argument_list|,
literal|"file:///a"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///b"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.classpath.files property was not "
operator|+
literal|"set correctly"
argument_list|,
literal|"file:/a,file:/b"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.cache.files property was not set "
operator|+
literal|"correctly"
argument_list|,
literal|"file:///a,file:///b"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now test with 3 args
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|conf
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Accepted null archives argument"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///a"
argument_list|)
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.classpath.files property was not "
operator|+
literal|"set correctly"
argument_list|,
literal|"file:/a"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.cache.files property was not set "
operator|+
literal|"correctly"
argument_list|,
literal|"file:///a"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///b"
argument_list|)
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.classpath.files property was not "
operator|+
literal|"set correctly"
argument_list|,
literal|"file:/a,file:/b"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.cache.files property was not set "
operator|+
literal|"correctly"
argument_list|,
literal|"file:///a,file:///b"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now test with 4th arg true
name|conf
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Accepted null archives argument"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///a"
argument_list|)
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.classpath.files property was not "
operator|+
literal|"set correctly"
argument_list|,
literal|"file:/a"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.cache.files property was not set "
operator|+
literal|"correctly"
argument_list|,
literal|"file:///a"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///b"
argument_list|)
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.classpath.files property was not "
operator|+
literal|"set correctly"
argument_list|,
literal|"file:/a,file:/b"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.cache.files property was not set "
operator|+
literal|"correctly"
argument_list|,
literal|"file:///a,file:///b"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
argument_list|)
expr_stmt|;
comment|// And finally with 4th arg false
name|conf
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Accepted null archives argument"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///a"
argument_list|)
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.classpath.files property was not "
operator|+
literal|"set correctly"
argument_list|,
literal|"file:/a"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.cache.files property was not set "
operator|+
literal|"correctly"
argument_list|,
literal|""
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addFileToClassPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///b"
argument_list|)
argument_list|,
name|conf
argument_list|,
name|fs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.classpath.files property was not "
operator|+
literal|"set correctly"
argument_list|,
literal|"file:/a,file:/b"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CLASSPATH_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The mapreduce.job.cache.files property was not set "
operator|+
literal|"correctly"
argument_list|,
literal|""
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

