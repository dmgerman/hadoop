begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|util
operator|.
name|DiskChecker
operator|.
name|DiskErrorException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * ReadWriteDiskValidator is the class to check a directory by to create a file,  * write some bytes into it, read back, and verify if they are identical.  * Read time and write time are recorded and put into an  * {@link ReadWriteDiskValidatorMetrics}.  */
end_comment

begin_class
DECL|class|ReadWriteDiskValidator
specifier|public
class|class
name|ReadWriteDiskValidator
implements|implements
name|DiskValidator
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"read-write"
decl_stmt|;
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|checkStatus (File dir)
specifier|public
name|void
name|checkStatus
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|DiskErrorException
block|{
name|ReadWriteDiskValidatorMetrics
name|metric
init|=
name|ReadWriteDiskValidatorMetrics
operator|.
name|getMetric
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|tmpFile
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|metric
operator|.
name|diskCheckFailed
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|DiskErrorException
argument_list|(
name|dir
operator|+
literal|" is not a directory!"
argument_list|)
throw|;
block|}
comment|// check the directory presence and permission.
name|DiskChecker
operator|.
name|checkDir
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// create a tmp file under the dir
name|tmpFile
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|dir
operator|.
name|toPath
argument_list|()
argument_list|,
literal|"test"
argument_list|,
literal|"tmp"
argument_list|)
expr_stmt|;
comment|// write 16 bytes into the tmp file
name|byte
index|[]
name|inputBytes
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
name|RANDOM
operator|.
name|nextBytes
argument_list|(
name|inputBytes
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|tmpFile
argument_list|,
name|inputBytes
argument_list|)
expr_stmt|;
name|long
name|writeLatency
init|=
name|TimeUnit
operator|.
name|MICROSECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|metric
operator|.
name|addWriteFileLatency
argument_list|(
name|writeLatency
argument_list|)
expr_stmt|;
comment|// read back
name|startTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|byte
index|[]
name|outputBytes
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|tmpFile
argument_list|)
decl_stmt|;
name|long
name|readLatency
init|=
name|TimeUnit
operator|.
name|MICROSECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|metric
operator|.
name|addReadFileLatency
argument_list|(
name|readLatency
argument_list|)
expr_stmt|;
comment|// validation
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|inputBytes
argument_list|,
name|outputBytes
argument_list|)
condition|)
block|{
name|metric
operator|.
name|diskCheckFailed
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Data in file has been corrupted."
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|metric
operator|.
name|diskCheckFailed
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"Disk Check failed!"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// delete the file
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|tmpFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|metric
operator|.
name|diskCheckFailed
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|DiskErrorException
argument_list|(
literal|"File deletion failed!"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

