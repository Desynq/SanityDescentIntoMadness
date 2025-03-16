package croissantnova.sanitydim.capability;

import croissantnova.sanitydim.ActiveSanitySources;
import croissantnova.sanitydim.util.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class Sanity implements ISanity, IPassiveSanity, IPersistentSanity
{
    private boolean m_dirty = true;
    private int m_emAngerTimer;
    private float deathScore;
    private float m_sanityVal;
    private float m_passive;
    private Vec3 m_stuckMultiplier;

    private final int[] cooldowns = new int[ActiveSanitySources.AMOUNT];
    private final Map<Integer, Integer> m_itemCds = new HashMap<>();
    private final Map<Integer, Integer> m_brokenBlocksCds = new HashMap<>();

    @Override
    public void serializeNBT(CompoundTag tag)
    {
        tag.putFloat("sanity.sanity", m_sanityVal);
        tag.putInt("sanity.ender_man_anger_timer", m_emAngerTimer);
        tag.putFloat("sanity.death_count", deathScore);

        // TODO: do lazy serialization instead
        tag.putInt("sanity.sleeping", cooldowns[ActiveSanitySources.SLEEPING]);
        tag.putInt("sanity.baby_chicken_spawn", cooldowns[ActiveSanitySources.SPAWNING_BABY_CHICKEN]);
        tag.putInt("sanity.animal_breeding", cooldowns[ActiveSanitySources.BREEDING_ANIMALS]);
        tag.putInt("sanity.villager_trade", cooldowns[ActiveSanitySources.VILLAGER_TRADE]);
        tag.putInt("sanity.shearing", cooldowns[ActiveSanitySources.SHEARING]);
        tag.putInt("sanity.eating", cooldowns[ActiveSanitySources.EATING]);
        tag.putInt("sanity.fishing", cooldowns[ActiveSanitySources.FISHING]);
        tag.putInt("sanity.potting_flower", cooldowns[ActiveSanitySources.POTTING_FLOWER]);

        serializeItemCds(tag);
        serializeBrokenBlocksCds(tag);
    }

    @Override
    public void deserializeNBT(CompoundTag tag)
    {
        setSanity(tag.getFloat("sanity.sanity"));
        setEnderManAngerTimer(tag.getInt("sanity.ender_man_anger_timer"));
        setDeathScore(tag.getFloat("sanity.death_count"));

        cooldowns[ActiveSanitySources.SLEEPING] = tag.getInt("sanity.sleeping");
        cooldowns[ActiveSanitySources.SPAWNING_BABY_CHICKEN] = tag.getInt("sanity.baby_chicken_spawn");
        cooldowns[ActiveSanitySources.BREEDING_ANIMALS] = tag.getInt("sanity.animal_breeding");
        cooldowns[ActiveSanitySources.VILLAGER_TRADE] = tag.getInt("sanity.villager_trade");
        cooldowns[ActiveSanitySources.SHEARING] = tag.getInt("sanity.shearing");
        cooldowns[ActiveSanitySources.EATING] = tag.getInt("sanity.eating");
        cooldowns[ActiveSanitySources.FISHING] = tag.getInt("sanity.fishing");
        cooldowns[ActiveSanitySources.POTTING_FLOWER] = tag.getInt("sanity.potting_flower");

        deserializeItemCds(tag);
        deserializeBrokenBlocksCooldowns(tag);
    }

    public void serialize(FriendlyByteBuf buf)
    {
        buf.writeFloat(m_sanityVal);
        buf.writeFloat(m_passive);
    }

    public void deserialize(FriendlyByteBuf buf)
    {
        m_sanityVal = buf.readFloat();
        m_passive = buf.readFloat();
    }

    @Override
    public float getSanity()
    {
        return m_sanityVal;
    }

    @Override
    public void setSanity(float value)
    {
        m_sanityVal = MathHelper.clampNorm(value);
        m_dirty = true;
    }

    @Override
    public float getPassiveIncrease()
    {
        return m_passive;
    }

    @Override
    public void setPassiveIncrease(float value)
    {
        m_passive = value;
        m_dirty = true;
    }

    public boolean getDirty()
    {
        return m_dirty;
    }

    public void setDirty(boolean value)
    {
        m_dirty = value;
    }

    @Override
    public int[] getActiveSourcesCooldowns()
    {
        return cooldowns;
    }

    @Override
    public Map<Integer, Integer> getItemCooldowns()
    {
        return m_itemCds;
    }

    @Override
    public Map<Integer, Integer> getBrokenBlocksCooldowns()
    {
        return m_brokenBlocksCds;
    }

    @Override
    public void setEnderManAngerTimer(int value)
    {
        m_emAngerTimer = value;
    }

    @Override
    public int getEnderManAngerTimer()
    {
        return m_emAngerTimer;
    }

    @Override
    public void setDeathScore(float value) {
        deathScore = value;
    }

    @Override
    public float getDeathScore() {
        return deathScore;
    }

    @Override
    public void setStuckMotionMultiplier(Vec3 multiplier)
    {
        m_stuckMultiplier = multiplier;
    }

    @Override
    public Vec3 getStuckMotionMultiplier()
    {
        return m_stuckMultiplier;
    }

    private void serializeItemCds(CompoundTag tag)
    {
        long[] itemCds = new long[m_itemCds.size()];
        int i = 0;

        for (Map.Entry<Integer, Integer> entry : m_itemCds.entrySet())
        {
            long val = entry.getKey();
            val <<= Long.SIZE / 2;
            val |= entry.getValue();

            itemCds[i] = val;

            i++;
        }

        tag.putLongArray("sanity.item_cooldowns", itemCds);
    }

    private void deserializeItemCds(CompoundTag tag)
    {
        long[] itemCds = tag.getLongArray("sanity.item_cooldowns");
        m_itemCds.clear();

        for (long itemCd : itemCds)
        {
            m_itemCds.put((int)(itemCd >> Long.SIZE / 2), (int)itemCd);
        }
    }

    private void serializeBrokenBlocksCds(CompoundTag tag)
    {
        long[] brokenBlocksCds = new long[m_brokenBlocksCds.size()];
        int i = 0;

        for (Map.Entry<Integer, Integer> entry : m_brokenBlocksCds.entrySet())
        {
            long val = entry.getKey();
            val <<= Long.SIZE / 2;
            val |= entry.getValue();

            brokenBlocksCds[i] = val;

            i++;
        }

        tag.putLongArray("sanity.broken_blocks_cooldowns", brokenBlocksCds);
    }

    private void deserializeBrokenBlocksCooldowns(CompoundTag tag)
    {
        long[] brokenBlocksCds = tag.getLongArray("sanity.broken_blocks_cooldowns");
        m_brokenBlocksCds.clear();

        for (long blockCd : brokenBlocksCds)
        {
            m_itemCds.put((int)(blockCd >> Long.SIZE / 2), (int)blockCd);
        }
    }
}