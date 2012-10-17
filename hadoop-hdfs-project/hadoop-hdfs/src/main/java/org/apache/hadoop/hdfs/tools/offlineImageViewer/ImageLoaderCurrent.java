begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|permission
operator|.
name|FsPermission
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeInfo
operator|.
name|AdminStates
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
name|hdfs
operator|.
name|protocol
operator|.
name|LayoutVersion
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
name|hdfs
operator|.
name|protocol
operator|.
name|LayoutVersion
operator|.
name|Feature
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSImageSerialization
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
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
operator|.
name|ImageVisitor
operator|.
name|ImageElement
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|WritableUtils
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
name|io
operator|.
name|compress
operator|.
name|CompressionCodec
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
name|io
operator|.
name|compress
operator|.
name|CompressionCodecFactory
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationKey
import|;
end_import

begin_comment
comment|/**  * ImageLoaderCurrent processes Hadoop FSImage files and walks over  * them using a provided ImageVisitor, calling the visitor at each element  * enumerated below.  *  * The only difference between v18 and v19 was the utilization of the  * stickybit.  Therefore, the same viewer can reader either format.  *  * Versions -19 fsimage layout (with changes from -16 up):  * Image version (int)  * Namepsace ID (int)  * NumFiles (long)  * Generation stamp (long)  * INodes (count = NumFiles)  *  INode  *    Path (String)  *    Replication (short)  *    Modification Time (long as date)  *    Access Time (long) // added in -16  *    Block size (long)  *    Num blocks (int)  *    Blocks (count = Num blocks)  *      Block  *        Block ID (long)  *        Num bytes (long)  *        Generation stamp (long)  *    Namespace Quota (long)  *    Diskspace Quota (long) // added in -18  *    Permissions  *      Username (String)  *      Groupname (String)  *      OctalPerms (short -> String)  // Modified in -19  *    Symlink (String) // added in -23  * NumINodesUnderConstruction (int)  * INodesUnderConstruction (count = NumINodesUnderConstruction)  *  INodeUnderConstruction  *    Path (bytes as string)  *    Replication (short)  *    Modification time (long as date)  *    Preferred block size (long)  *    Num blocks (int)  *    Blocks  *      Block  *        Block ID (long)  *        Num bytes (long)  *        Generation stamp (long)  *    Permissions  *      Username (String)  *      Groupname (String)  *      OctalPerms (short -> String)  *    Client Name (String)  *    Client Machine (String)  *    NumLocations (int)  *    DatanodeDescriptors (count = numLocations) // not loaded into memory  *      short                                    // but still in file  *      long  *      string  *      long  *      int  *      string  *      string  *      enum  *    CurrentDelegationKeyId (int)  *    NumDelegationKeys (int)  *      DelegationKeys (count = NumDelegationKeys)  *        DelegationKeyLength (vint)  *        DelegationKey (bytes)  *    DelegationTokenSequenceNumber (int)  *    NumDelegationTokens (int)  *    DelegationTokens (count = NumDelegationTokens)  *      DelegationTokenIdentifier  *        owner (String)  *        renewer (String)  *        realUser (String)  *        issueDate (vlong)  *        maxDate (vlong)  *        sequenceNumber (vint)  *        masterKeyId (vint)  *      expiryTime (long)       *  */
end_comment

begin_class
DECL|class|ImageLoaderCurrent
class|class
name|ImageLoaderCurrent
implements|implements
name|ImageLoader
block|{
DECL|field|dateFormat
specifier|protected
specifier|final
name|DateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm"
argument_list|)
decl_stmt|;
DECL|field|versions
specifier|private
specifier|static
name|int
index|[]
name|versions
init|=
block|{
operator|-
literal|16
block|,
operator|-
literal|17
block|,
operator|-
literal|18
block|,
operator|-
literal|19
block|,
operator|-
literal|20
block|,
operator|-
literal|21
block|,
operator|-
literal|22
block|,
operator|-
literal|23
block|,
operator|-
literal|24
block|,
operator|-
literal|25
block|,
operator|-
literal|26
block|,
operator|-
literal|27
block|,
operator|-
literal|28
block|,
operator|-
literal|30
block|,
operator|-
literal|31
block|,
operator|-
literal|32
block|,
operator|-
literal|33
block|,
operator|-
literal|34
block|,
operator|-
literal|35
block|,
operator|-
literal|36
block|,
operator|-
literal|37
block|,
operator|-
literal|38
block|,
operator|-
literal|39
block|,
operator|-
literal|40
block|}
decl_stmt|;
DECL|field|imageVersion
specifier|private
name|int
name|imageVersion
init|=
literal|0
decl_stmt|;
comment|/* (non-Javadoc)    * @see ImageLoader#canProcessVersion(int)    */
annotation|@
name|Override
DECL|method|canLoadVersion (int version)
specifier|public
name|boolean
name|canLoadVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
for|for
control|(
name|int
name|v
range|:
name|versions
control|)
if|if
condition|(
name|v
operator|==
name|version
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc)    * @see ImageLoader#processImage(java.io.DataInputStream, ImageVisitor, boolean)    */
annotation|@
name|Override
DECL|method|loadImage (DataInputStream in, ImageVisitor v, boolean skipBlocks)
specifier|public
name|void
name|loadImage
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|,
name|boolean
name|skipBlocks
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|done
init|=
literal|false
decl_stmt|;
try|try
block|{
name|v
operator|.
name|start
argument_list|()
expr_stmt|;
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|FS_IMAGE
argument_list|)
expr_stmt|;
name|imageVersion
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|canLoadVersion
argument_list|(
name|imageVersion
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot process fslayout version "
operator|+
name|imageVersion
argument_list|)
throw|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|IMAGE_VERSION
argument_list|,
name|imageVersion
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|NAMESPACE_ID
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|numInodes
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|GENERATION_STAMP
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|STORED_TXIDS
argument_list|,
name|imageVersion
argument_list|)
condition|)
block|{
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|TRANSACTION_ID
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|FSIMAGE_COMPRESSION
argument_list|,
name|imageVersion
argument_list|)
condition|)
block|{
name|boolean
name|isCompressed
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|IS_COMPRESSED
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|isCompressed
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCompressed
condition|)
block|{
name|String
name|codecClassName
init|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|COMPRESS_CODEC
argument_list|,
name|codecClassName
argument_list|)
expr_stmt|;
name|CompressionCodecFactory
name|codecFac
init|=
operator|new
name|CompressionCodecFactory
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|CompressionCodec
name|codec
init|=
name|codecFac
operator|.
name|getCodecByClassName
argument_list|(
name|codecClassName
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Image compression codec not supported: "
operator|+
name|codecClassName
argument_list|)
throw|;
block|}
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|codec
operator|.
name|createInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|processINodes
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|numInodes
argument_list|,
name|skipBlocks
argument_list|)
expr_stmt|;
name|processINodesUC
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|skipBlocks
argument_list|)
expr_stmt|;
if|if
condition|(
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|DELEGATION_TOKEN
argument_list|,
name|imageVersion
argument_list|)
condition|)
block|{
name|processDelegationTokens
argument_list|(
name|in
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// FSImage
name|done
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|done
condition|)
block|{
name|v
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|v
operator|.
name|finishAbnormally
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Process the Delegation Token related section in fsimage.    *     * @param in DataInputStream to process    * @param v Visitor to walk over records    */
DECL|method|processDelegationTokens (DataInputStream in, ImageVisitor v)
specifier|private
name|void
name|processDelegationTokens
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|CURRENT_DELEGATION_KEY_ID
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numDKeys
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_KEYS
argument_list|,
name|ImageElement
operator|.
name|NUM_DELEGATION_KEYS
argument_list|,
name|numDKeys
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDKeys
condition|;
name|i
operator|++
control|)
block|{
name|DelegationKey
name|key
init|=
operator|new
name|DelegationKey
argument_list|()
decl_stmt|;
name|key
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_KEY
argument_list|,
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_SEQUENCE_NUMBER
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numDTokens
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKENS
argument_list|,
name|ImageElement
operator|.
name|NUM_DELEGATION_TOKENS
argument_list|,
name|numDTokens
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDTokens
condition|;
name|i
operator|++
control|)
block|{
name|DelegationTokenIdentifier
name|id
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|id
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|long
name|expiryTime
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_KIND
argument_list|,
name|id
operator|.
name|getKind
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_SEQNO
argument_list|,
name|id
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_OWNER
argument_list|,
name|id
operator|.
name|getOwner
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_RENEWER
argument_list|,
name|id
operator|.
name|getRenewer
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_REALUSER
argument_list|,
name|id
operator|.
name|getRealUser
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_ISSUE_DATE
argument_list|,
name|id
operator|.
name|getIssueDate
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_MAX_DATE
argument_list|,
name|id
operator|.
name|getMaxDate
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_EXPIRY_TIME
argument_list|,
name|expiryTime
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DELEGATION_TOKEN_IDENTIFIER_MASTER_KEY_ID
argument_list|,
name|id
operator|.
name|getMasterKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// DELEGATION_TOKEN_IDENTIFIER
block|}
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// DELEGATION_TOKENS
block|}
comment|/**    * Process the INodes under construction section of the fsimage.    *    * @param in DataInputStream to process    * @param v Visitor to walk over inodes    * @param skipBlocks Walk over each block?    */
DECL|method|processINodesUC (DataInputStream in, ImageVisitor v, boolean skipBlocks)
specifier|private
name|void
name|processINodesUC
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|,
name|boolean
name|skipBlocks
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numINUC
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|INODES_UNDER_CONSTRUCTION
argument_list|,
name|ImageElement
operator|.
name|NUM_INODES_UNDER_CONSTRUCTION
argument_list|,
name|numINUC
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numINUC
condition|;
name|i
operator|++
control|)
block|{
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|INODE_UNDER_CONSTRUCTION
argument_list|)
expr_stmt|;
name|byte
index|[]
name|name
init|=
name|FSImageSerialization
operator|.
name|readBytes
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|String
name|n
init|=
operator|new
name|String
argument_list|(
name|name
argument_list|,
literal|"UTF8"
argument_list|)
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|INODE_PATH
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|REPLICATION
argument_list|,
name|in
operator|.
name|readShort
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|MODIFICATION_TIME
argument_list|,
name|formatDate
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|PREFERRED_BLOCK_SIZE
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numBlocks
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|processBlocks
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|numBlocks
argument_list|,
name|skipBlocks
argument_list|)
expr_stmt|;
name|processPermission
argument_list|(
name|in
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|CLIENT_NAME
argument_list|,
name|FSImageSerialization
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|CLIENT_MACHINE
argument_list|,
name|FSImageSerialization
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
comment|// Skip over the datanode descriptors, which are still stored in the
comment|// file but are not used by the datanode or loaded into memory
name|int
name|numLocs
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numLocs
condition|;
name|j
operator|++
control|)
block|{
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|FSImageSerialization
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|FSImageSerialization
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|AdminStates
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// INodeUnderConstruction
block|}
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// INodesUnderConstruction
block|}
comment|/**    * Process the blocks section of the fsimage.    *    * @param in Datastream to process    * @param v Visitor to walk over inodes    * @param skipBlocks Walk over each block?    */
DECL|method|processBlocks (DataInputStream in, ImageVisitor v, int numBlocks, boolean skipBlocks)
specifier|private
name|void
name|processBlocks
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|,
name|int
name|numBlocks
parameter_list|,
name|boolean
name|skipBlocks
parameter_list|)
throws|throws
name|IOException
block|{
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|BLOCKS
argument_list|,
name|ImageElement
operator|.
name|NUM_BLOCKS
argument_list|,
name|numBlocks
argument_list|)
expr_stmt|;
comment|// directory or symlink, no blocks to process
if|if
condition|(
name|numBlocks
operator|==
operator|-
literal|1
operator|||
name|numBlocks
operator|==
operator|-
literal|2
condition|)
block|{
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// Blocks
return|return;
block|}
if|if
condition|(
name|skipBlocks
condition|)
block|{
name|int
name|bytesToSkip
init|=
operator|(
operator|(
name|Long
operator|.
name|SIZE
operator|*
literal|3
comment|/* fields */
operator|)
operator|/
literal|8
comment|/*bits*/
operator|)
operator|*
name|numBlocks
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|skipBytes
argument_list|(
name|bytesToSkip
argument_list|)
operator|!=
name|bytesToSkip
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error skipping over blocks"
argument_list|)
throw|;
block|}
else|else
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numBlocks
condition|;
name|j
operator|++
control|)
block|{
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|BLOCK
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|BLOCK_ID
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|NUM_BYTES
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|GENERATION_STAMP
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// Block
block|}
block|}
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// Blocks
block|}
comment|/**    * Extract the INode permissions stored in the fsimage file.    *    * @param in Datastream to process    * @param v Visitor to walk over inodes    */
DECL|method|processPermission (DataInputStream in, ImageVisitor v)
specifier|private
name|void
name|processPermission
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|PERMISSIONS
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|USER_NAME
argument_list|,
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|GROUP_NAME
argument_list|,
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|FsPermission
name|fsp
init|=
operator|new
name|FsPermission
argument_list|(
name|in
operator|.
name|readShort
argument_list|()
argument_list|)
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|PERMISSION_STRING
argument_list|,
name|fsp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// Permissions
block|}
comment|/**    * Process the INode records stored in the fsimage.    *    * @param in Datastream to process    * @param v Visitor to walk over INodes    * @param numInodes Number of INodes stored in file    * @param skipBlocks Process all the blocks within the INode?    * @throws VisitException    * @throws IOException    */
DECL|method|processINodes (DataInputStream in, ImageVisitor v, long numInodes, boolean skipBlocks)
specifier|private
name|void
name|processINodes
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|,
name|long
name|numInodes
parameter_list|,
name|boolean
name|skipBlocks
parameter_list|)
throws|throws
name|IOException
block|{
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|INODES
argument_list|,
name|ImageElement
operator|.
name|NUM_INODES
argument_list|,
name|numInodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|FSIMAGE_NAME_OPTIMIZATION
argument_list|,
name|imageVersion
argument_list|)
condition|)
block|{
name|processLocalNameINodes
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|numInodes
argument_list|,
name|skipBlocks
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// full path name
name|processFullNameINodes
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|numInodes
argument_list|,
name|skipBlocks
argument_list|)
expr_stmt|;
block|}
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// INodes
block|}
comment|/**    * Process image with full path name    *     * @param in image stream    * @param v visitor    * @param numInodes number of indoes to read    * @param skipBlocks skip blocks or not    * @throws IOException if there is any error occurs    */
DECL|method|processLocalNameINodes (DataInputStream in, ImageVisitor v, long numInodes, boolean skipBlocks)
specifier|private
name|void
name|processLocalNameINodes
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|,
name|long
name|numInodes
parameter_list|,
name|boolean
name|skipBlocks
parameter_list|)
throws|throws
name|IOException
block|{
comment|// process root
name|processINode
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|skipBlocks
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|numInodes
operator|--
expr_stmt|;
while|while
condition|(
name|numInodes
operator|>
literal|0
condition|)
block|{
name|numInodes
operator|-=
name|processDirectory
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|skipBlocks
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processDirectory (DataInputStream in, ImageVisitor v, boolean skipBlocks)
specifier|private
name|int
name|processDirectory
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|,
name|boolean
name|skipBlocks
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|parentName
init|=
name|FSImageSerialization
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|int
name|numChildren
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numChildren
condition|;
name|i
operator|++
control|)
block|{
name|processINode
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|skipBlocks
argument_list|,
name|parentName
argument_list|)
expr_stmt|;
block|}
return|return
name|numChildren
return|;
block|}
comment|/**     * Process image with full path name     *      * @param in image stream     * @param v visitor     * @param numInodes number of indoes to read     * @param skipBlocks skip blocks or not     * @throws IOException if there is any error occurs     */
DECL|method|processFullNameINodes (DataInputStream in, ImageVisitor v, long numInodes, boolean skipBlocks)
specifier|private
name|void
name|processFullNameINodes
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|,
name|long
name|numInodes
parameter_list|,
name|boolean
name|skipBlocks
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numInodes
condition|;
name|i
operator|++
control|)
block|{
name|processINode
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|skipBlocks
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Process an INode     *      * @param in image stream     * @param v visitor     * @param skipBlocks skip blocks or not     * @param parentName the name of its parent node     * @throws IOException     */
DECL|method|processINode (DataInputStream in, ImageVisitor v, boolean skipBlocks, String parentName)
specifier|private
name|void
name|processINode
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|ImageVisitor
name|v
parameter_list|,
name|boolean
name|skipBlocks
parameter_list|,
name|String
name|parentName
parameter_list|)
throws|throws
name|IOException
block|{
name|v
operator|.
name|visitEnclosingElement
argument_list|(
name|ImageElement
operator|.
name|INODE
argument_list|)
expr_stmt|;
name|String
name|pathName
init|=
name|FSImageSerialization
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentName
operator|!=
literal|null
condition|)
block|{
comment|// local name
name|pathName
operator|=
literal|"/"
operator|+
name|pathName
expr_stmt|;
if|if
condition|(
operator|!
literal|"/"
operator|.
name|equals
argument_list|(
name|parentName
argument_list|)
condition|)
block|{
comment|// children of non-root directory
name|pathName
operator|=
name|parentName
operator|+
name|pathName
expr_stmt|;
block|}
block|}
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|INODE_PATH
argument_list|,
name|pathName
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|REPLICATION
argument_list|,
name|in
operator|.
name|readShort
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|MODIFICATION_TIME
argument_list|,
name|formatDate
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|FILE_ACCESS_TIME
argument_list|,
name|imageVersion
argument_list|)
condition|)
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|ACCESS_TIME
argument_list|,
name|formatDate
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|BLOCK_SIZE
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numBlocks
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|processBlocks
argument_list|(
name|in
argument_list|,
name|v
argument_list|,
name|numBlocks
argument_list|,
name|skipBlocks
argument_list|)
expr_stmt|;
comment|// File or directory
if|if
condition|(
name|numBlocks
operator|>
literal|0
operator|||
name|numBlocks
operator|==
operator|-
literal|1
condition|)
block|{
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|NS_QUOTA
argument_list|,
name|numBlocks
operator|==
operator|-
literal|1
condition|?
name|in
operator|.
name|readLong
argument_list|()
else|:
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|DISKSPACE_QUOTA
argument_list|,
name|imageVersion
argument_list|)
condition|)
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|DS_QUOTA
argument_list|,
name|numBlocks
operator|==
operator|-
literal|1
condition|?
name|in
operator|.
name|readLong
argument_list|()
else|:
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numBlocks
operator|==
operator|-
literal|2
condition|)
block|{
name|v
operator|.
name|visit
argument_list|(
name|ImageElement
operator|.
name|SYMLINK
argument_list|,
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|processPermission
argument_list|(
name|in
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|v
operator|.
name|leaveEnclosingElement
argument_list|()
expr_stmt|;
comment|// INode
block|}
comment|/**    * Helper method to format dates during processing.    * @param date Date as read from image file    * @return String version of date format    */
DECL|method|formatDate (long date)
specifier|private
name|String
name|formatDate
parameter_list|(
name|long
name|date
parameter_list|)
block|{
return|return
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|date
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

