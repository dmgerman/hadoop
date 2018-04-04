begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.ozone.genesis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
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
name|lang
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
name|utils
operator|.
name|MetadataStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Benchmark
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Param
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Setup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|State
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|infra
operator|.
name|Blackhole
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
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
operator|.
name|GenesisUtil
operator|.
name|CACHE_10MB_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
operator|.
name|GenesisUtil
operator|.
name|CACHE_1GB_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
operator|.
name|GenesisUtil
operator|.
name|CLOSED_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
operator|.
name|GenesisUtil
operator|.
name|DEFAULT_TYPE
import|;
end_import

begin_class
annotation|@
name|State
argument_list|(
name|Scope
operator|.
name|Thread
argument_list|)
DECL|class|BenchMarkMetadataStoreReads
specifier|public
class|class
name|BenchMarkMetadataStoreReads
block|{
DECL|field|DATA_LEN
specifier|private
specifier|static
specifier|final
name|int
name|DATA_LEN
init|=
literal|1024
decl_stmt|;
DECL|field|maxKeys
specifier|private
specifier|static
specifier|final
name|long
name|maxKeys
init|=
literal|1024
operator|*
literal|10
decl_stmt|;
DECL|field|store
specifier|private
name|MetadataStore
name|store
decl_stmt|;
annotation|@
name|Param
argument_list|(
block|{
name|DEFAULT_TYPE
block|,
name|CACHE_10MB_TYPE
block|,
name|CACHE_1GB_TYPE
block|,
name|CLOSED_TYPE
block|}
argument_list|)
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
annotation|@
name|Setup
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
name|store
operator|=
name|GenesisUtil
operator|.
name|getMetadataStore
argument_list|(
name|this
operator|.
name|type
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
name|DATA_LEN
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|maxKeys
condition|;
name|x
operator|++
control|)
block|{
name|store
operator|.
name|put
argument_list|(
name|Long
operator|.
name|toHexString
argument_list|(
name|x
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|compareTo
argument_list|(
name|CLOSED_TYPE
argument_list|)
operator|==
literal|0
condition|)
block|{
name|store
operator|.
name|compactDB
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Benchmark
DECL|method|test (Blackhole bh)
specifier|public
name|void
name|test
parameter_list|(
name|Blackhole
name|bh
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|x
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomUtils
operator|.
name|nextLong
argument_list|(
literal|0L
argument_list|,
name|maxKeys
argument_list|)
decl_stmt|;
name|bh
operator|.
name|consume
argument_list|(
name|store
operator|.
name|get
argument_list|(
name|Long
operator|.
name|toHexString
argument_list|(
name|x
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

