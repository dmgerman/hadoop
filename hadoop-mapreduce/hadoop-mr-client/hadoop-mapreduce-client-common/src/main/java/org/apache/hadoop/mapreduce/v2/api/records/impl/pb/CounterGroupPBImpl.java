begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.records.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|Counter
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|CounterGroup
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
name|v2
operator|.
name|proto
operator|.
name|MRProtos
operator|.
name|CounterGroupProto
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
name|v2
operator|.
name|proto
operator|.
name|MRProtos
operator|.
name|CounterGroupProtoOrBuilder
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
name|v2
operator|.
name|proto
operator|.
name|MRProtos
operator|.
name|CounterProto
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
name|v2
operator|.
name|proto
operator|.
name|MRProtos
operator|.
name|StringCounterMapProto
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ProtoBase
import|;
end_import

begin_class
DECL|class|CounterGroupPBImpl
specifier|public
class|class
name|CounterGroupPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|CounterGroupProto
argument_list|>
implements|implements
name|CounterGroup
block|{
DECL|field|proto
name|CounterGroupProto
name|proto
init|=
name|CounterGroupProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|CounterGroupProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|field|counters
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|counters
init|=
literal|null
decl_stmt|;
DECL|method|CounterGroupPBImpl ()
specifier|public
name|CounterGroupPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|CounterGroupProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|CounterGroupPBImpl (CounterGroupProto proto)
specifier|public
name|CounterGroupPBImpl
parameter_list|(
name|CounterGroupProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|CounterGroupProto
name|getProto
parameter_list|()
block|{
name|mergeLocalToProto
argument_list|()
expr_stmt|;
name|proto
operator|=
name|viaProto
condition|?
name|proto
else|:
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
return|return
name|proto
return|;
block|}
DECL|method|mergeLocalToBuilder ()
specifier|private
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|counters
operator|!=
literal|null
condition|)
block|{
name|addContersToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mergeLocalToProto ()
specifier|private
name|void
name|mergeLocalToProto
parameter_list|()
block|{
if|if
condition|(
name|viaProto
condition|)
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|mergeLocalToBuilder
argument_list|()
expr_stmt|;
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|maybeInitBuilder ()
specifier|private
name|void
name|maybeInitBuilder
parameter_list|()
block|{
if|if
condition|(
name|viaProto
operator|||
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
name|CounterGroupProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
name|viaProto
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
name|CounterGroupProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|hasName
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getName
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearName
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setName
argument_list|(
operator|(
name|name
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDisplayName ()
specifier|public
name|String
name|getDisplayName
parameter_list|()
block|{
name|CounterGroupProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|hasDisplayName
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getDisplayName
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDisplayName (String displayName)
specifier|public
name|void
name|setDisplayName
parameter_list|(
name|String
name|displayName
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|displayName
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearDisplayName
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setDisplayName
argument_list|(
operator|(
name|displayName
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAllCounters ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|getAllCounters
parameter_list|()
block|{
name|initCounters
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|counters
return|;
block|}
annotation|@
name|Override
DECL|method|getCounter (String key)
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|initCounters
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|counters
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|initCounters ()
specifier|private
name|void
name|initCounters
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|counters
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|CounterGroupProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|List
argument_list|<
name|StringCounterMapProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getCountersList
argument_list|()
decl_stmt|;
name|this
operator|.
name|counters
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|StringCounterMapProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|counters
operator|.
name|put
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
argument_list|,
name|convertFromProtoFormat
argument_list|(
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addAllCounters (final Map<String, Counter> counters)
specifier|public
name|void
name|addAllCounters
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|counters
parameter_list|)
block|{
if|if
condition|(
name|counters
operator|==
literal|null
condition|)
return|return;
name|initCounters
argument_list|()
expr_stmt|;
name|this
operator|.
name|counters
operator|.
name|putAll
argument_list|(
name|counters
argument_list|)
expr_stmt|;
block|}
DECL|method|addContersToProto ()
specifier|private
name|void
name|addContersToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearCounters
argument_list|()
expr_stmt|;
if|if
condition|(
name|counters
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|StringCounterMapProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|StringCounterMapProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|StringCounterMapProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|StringCounterMapProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|keyIter
init|=
name|counters
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|StringCounterMapProto
name|next
parameter_list|()
block|{
name|String
name|key
init|=
name|keyIter
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|StringCounterMapProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|key
argument_list|)
operator|.
name|setValue
argument_list|(
name|convertToProtoFormat
argument_list|(
name|counters
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|keyIter
operator|.
name|hasNext
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|builder
operator|.
name|addAllCounters
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCounter (String key, Counter val)
specifier|public
name|void
name|setCounter
parameter_list|(
name|String
name|key
parameter_list|,
name|Counter
name|val
parameter_list|)
block|{
name|initCounters
argument_list|()
expr_stmt|;
name|this
operator|.
name|counters
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeCounter (String key)
specifier|public
name|void
name|removeCounter
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|initCounters
argument_list|()
expr_stmt|;
name|this
operator|.
name|counters
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearCounters ()
specifier|public
name|void
name|clearCounters
parameter_list|()
block|{
name|initCounters
argument_list|()
expr_stmt|;
name|this
operator|.
name|counters
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (CounterProto p)
specifier|private
name|CounterPBImpl
name|convertFromProtoFormat
parameter_list|(
name|CounterProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|CounterPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Counter t)
specifier|private
name|CounterProto
name|convertToProtoFormat
parameter_list|(
name|Counter
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|CounterPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

