begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|common
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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

begin_comment
comment|/**  * Tests for {@link Checksum} class.  */
end_comment

begin_class
DECL|class|TestChecksum
specifier|public
class|class
name|TestChecksum
block|{
DECL|field|BYTES_PER_CHECKSUM
specifier|private
specifier|static
specifier|final
name|int
name|BYTES_PER_CHECKSUM
init|=
literal|10
decl_stmt|;
DECL|field|CHECKSUM_TYPE_DEFAULT
specifier|private
specifier|static
specifier|final
name|ContainerProtos
operator|.
name|ChecksumType
name|CHECKSUM_TYPE_DEFAULT
init|=
name|ContainerProtos
operator|.
name|ChecksumType
operator|.
name|SHA256
decl_stmt|;
DECL|method|getChecksum (ContainerProtos.ChecksumType type)
specifier|private
name|Checksum
name|getChecksum
parameter_list|(
name|ContainerProtos
operator|.
name|ChecksumType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|CHECKSUM_TYPE_DEFAULT
expr_stmt|;
block|}
return|return
operator|new
name|Checksum
argument_list|(
name|type
argument_list|,
name|BYTES_PER_CHECKSUM
argument_list|)
return|;
block|}
comment|/**    * Tests {@link Checksum#verifyChecksum(byte[], ChecksumData)}.    */
annotation|@
name|Test
DECL|method|testVerifyChecksum ()
specifier|public
name|void
name|testVerifyChecksum
parameter_list|()
throws|throws
name|Exception
block|{
name|Checksum
name|checksum
init|=
name|getChecksum
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|dataLen
init|=
literal|55
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
name|dataLen
argument_list|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|ChecksumData
name|checksumData
init|=
name|checksum
operator|.
name|computeChecksum
argument_list|(
name|data
argument_list|)
decl_stmt|;
comment|// A checksum is calculate for each bytesPerChecksum number of bytes in
comment|// the data. Since that value is 10 here and the data length is 55, we
comment|// should have 6 checksums in checksumData.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|checksumData
operator|.
name|getChecksums
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Checksum verification should pass
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Checksum mismatch"
argument_list|,
name|Checksum
operator|.
name|verifyChecksum
argument_list|(
name|data
argument_list|,
name|checksumData
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that if data is modified, then the checksums should not match.    */
annotation|@
name|Test
DECL|method|testIncorrectChecksum ()
specifier|public
name|void
name|testIncorrectChecksum
parameter_list|()
throws|throws
name|Exception
block|{
name|Checksum
name|checksum
init|=
name|getChecksum
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|55
argument_list|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|ChecksumData
name|originalChecksumData
init|=
name|checksum
operator|.
name|computeChecksum
argument_list|(
name|data
argument_list|)
decl_stmt|;
comment|// Change the data and check if new checksum matches the original checksum.
comment|// Modifying one byte of data should be enough for the checksum data to
comment|// mismatch
name|data
index|[
literal|50
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|data
index|[
literal|50
index|]
operator|+
literal|1
argument_list|)
expr_stmt|;
name|ChecksumData
name|newChecksumData
init|=
name|checksum
operator|.
name|computeChecksum
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"Checksums should not match for different data"
argument_list|,
name|originalChecksumData
argument_list|,
name|newChecksumData
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that checksum calculated using two different checksumTypes should    * not match.    */
annotation|@
name|Test
DECL|method|testChecksumMismatchForDifferentChecksumTypes ()
specifier|public
name|void
name|testChecksumMismatchForDifferentChecksumTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|data
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|55
argument_list|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
comment|// Checksum1 of type SHA-256
name|Checksum
name|checksum1
init|=
name|getChecksum
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|ChecksumData
name|checksumData1
init|=
name|checksum1
operator|.
name|computeChecksum
argument_list|(
name|data
argument_list|)
decl_stmt|;
comment|// Checksum2 of type CRC32
name|Checksum
name|checksum2
init|=
name|getChecksum
argument_list|(
name|ContainerProtos
operator|.
name|ChecksumType
operator|.
name|CRC32
argument_list|)
decl_stmt|;
name|ChecksumData
name|checksumData2
init|=
name|checksum2
operator|.
name|computeChecksum
argument_list|(
name|data
argument_list|)
decl_stmt|;
comment|// The two checksums should not match as they have different types
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"Checksums should not match for different checksum types"
argument_list|,
name|checksum1
argument_list|,
name|checksum2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

